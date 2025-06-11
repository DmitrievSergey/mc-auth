package com.skillbox.auth.controller;


import com.skillbox.auth.dto.events.ChangeEmail;
import com.skillbox.auth.dto.events.ChangeEmailEvent;
import com.skillbox.auth.dto.events.UserRegistration;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import com.skillbox.auth.dto.request.*;
import com.skillbox.auth.dto.response.AuthenticationDtoResponse;
import com.skillbox.auth.dto.response.CaptchaResponse;
import com.skillbox.auth.dto.response.TokenRefreshResponse;
import com.skillbox.auth.entity.RefreshTokenEntity;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.exception.CaptchaDoesNotMatchException;
import com.skillbox.auth.exception.InvalidLoginOrPasswordException;
import com.skillbox.auth.exception.PasswordsDoesNotMatchException;
import com.skillbox.auth.exception.TokenRefreshException;
import com.skillbox.auth.security.AppUserDetails;
import com.skillbox.auth.service.CaptchaService;
import com.skillbox.auth.service.KafkaMessagePublisher;
import com.skillbox.auth.service.RefreshTokenService;
import com.skillbox.auth.service.UserService;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final CaptchaService captchaService;
    private final KafkaMessagePublisher kafkaMessagePublisher;
    @Value("${image.uploadDirectory}")
    String imageDirectory;

    @Value("${image.address}")
    String imageAddress;

    @GetMapping("/captcha")
    ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        log.info("Начинаем генерить imageId");
        String imageId = UUID.randomUUID().toString();
        log.info("imageId {}",imageId);
        captchaService.saveImageToStorage(imageDirectory, imageId);
        log.info("Image directory {}", imageDirectory);
        CaptchaResponse data = new CaptchaResponse(imageAddress.concat(imageId), imageId);

        log.info("Captcha data {}", data.getSecret());
        log.info("Captcha data {}", data.getImage());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDtoRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();

        log.info("Calling registerUser");
        log.info(request.toString());
        String captchaSecret = request.getCaptchaSecret();
        log.info(captchaSecret);
        if (!request.getCaptchaCode()
                .equals(captchaService.getCaptchaById(captchaSecret).getCaptchaCode())) {
            log.info("Каптча не совпадает. капча Юзера {}, реальная капча {}", request.getCaptchaCode(),
                    captchaService.getCaptchaById(request.getCaptchaSecret()).getCaptchaCode());
            throw new CaptchaDoesNotMatchException("Каптча не совпадает");
        }
        if (!request.getPassword1().equals(request.getPassword2())) {
            log.info("Пароли не совпадают");
            throw new PasswordsDoesNotMatchException("Пароли не совпадают");
        }

        log.info("Calling userService.saveUser");

        try {
            UserEntity user = userService.saveUser(request);
            sendUserRegisteredEvent(user);
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        }
        // TODO: если saveUser успешно, отправляем сообщение в кафку

        responseHeaders.set("Access-Control-Allow-Origin",
                "*");

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .build();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/login")
    ResponseEntity<AuthenticationDtoResponse> authUser(@Valid @RequestBody LoginDtoRequest request)
    {
        log.info("Начинаем аутентифицировать пользователя");
        HttpHeaders responseHeaders = new HttpHeaders();
        RefreshTokenEntity refreshToken = null;
        String token = null;
        try {
            log.info("Начинаем аутентифицировать пользователя");
            log.info("User name {}", request.getEmail());
            log.info("User password {}", request.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Аутентифицировали пользователя");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

            token = jwtTokenUtils.generateToken(userDetails);
            log.info("Сделали токен {}", token);

            log.info("User id = {}", userDetails.getId());

            refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            log.info("refreshToken = {}", refreshToken);

        } catch (BadCredentialsException exception) {
            throw new InvalidLoginOrPasswordException("Неправильный логин или пароль");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        responseHeaders
                .set("Accept", "media-type");
        responseHeaders
                .set("Content-Type", "application/json");


        return ResponseEntity
                .ok(new AuthenticationDtoResponse(token, refreshToken.getToken()));
    }


    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(AppUserDetails::build)
                .map(user -> {
                    String token = jwtTokenUtils.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String email = jwtTokenUtils.getUserName(request.getHeader("Authorization").substring(7));
        try {
            UserEntity user = userService.findUserByEmail(email);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User : {}", authentication.getName());
            log.info("Credentials : {}", authentication.getCredentials());

            log.info("Получили пользователя {}", user);
            log.info("Вызываем delete token");
            refreshTokenService.deleteToken(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.ok().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping ("/change-email-link")
    public ResponseEntity<?> changeEmailLink(@Valid @RequestBody EmailRequest request,
                                             @RequestHeader(name = "Authorization") String jwt) {
        String email = jwtTokenUtils.getUserName(jwt.substring(7));
        if(!email.equals(request.getEmail())) {
            UserEntity user = userService.findUserByEmail(email);
            refreshTokenService.deleteToken(user);
            user.setEmail(request.getEmail());
            //TODO: Поймать EmailAlreadyExists
            try{
                userService.saveUser(user);
                sendChangeEmailEvent(user);
            } catch (Exception e) {
                log.info(e.getLocalizedMessage());
            }

        }
        log.info("email {}",request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/change-password-link")
    public ResponseEntity<?> changePasswordLink(@Valid @RequestBody ChangePasswordRequest request
    , @RequestHeader(name = "Authorization") String jwt) {
        String email = jwtTokenUtils.getUserName(jwt.substring(7));
        UserEntity user = userService.findUserByEmail(email);
        log.info("email {}", email);
        log.info("User {}", user.getEmail());
        userService.saveUser(request, user);
        return ResponseEntity.ok().build();
    }

    private void sendChangeEmailEvent(UserEntity user) {
        ChangeEmailEvent event  = new ChangeEmailEvent();

        event.setChangeEmail(new ChangeEmail(
                user.getId()
                , user.getEmail()
        ));

        try{
            kafkaMessagePublisher.sendChangeEmailEvent(event);
            log.info("Message sent to kafka");
        } catch (KafkaException exception) {
            log.info("Message sent to kafka {}", exception.getMessage());
        }
    }

    private void sendUserRegisteredEvent(UserEntity user) {

        UserRegistrationEvent event = new UserRegistrationEvent();
        event.setUserRegistration(new UserRegistration(
                user.getId()
                , user.getEmail()
                , user.getFirstName()
                , user.getLastName()
                , user.getPassword()
                , user.getRoles()[0].substring(5)));

        try {
            kafkaMessagePublisher.sendUserRegistrationEvent(event);

            log.info("Message sent to kafka");
        } catch (KafkaException exception) {
            log.info("Message sent to kafka {}", exception.getMessage());
        }


    }

}
