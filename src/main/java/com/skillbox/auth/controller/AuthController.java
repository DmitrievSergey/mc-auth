package com.skillbox.auth.controller;

import cn.apiclub.captcha.Captcha;
import com.github.cage.Cage;
import com.skillbox.auth.dto.events.UserRegistration;
import com.skillbox.auth.dto.events.UserRegistrationEvent;
import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.dto.request.LoginDtoRequest;
import com.skillbox.auth.dto.request.TokenRefreshRequest;
import com.skillbox.auth.dto.response.AuthenticationDtoResponse;
import com.skillbox.auth.dto.response.CaptchaResponse;
import com.skillbox.auth.dto.response.TokenRefreshResponse;
import com.skillbox.auth.entity.RefreshTokenEntity;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.exception.CaptchaDoesNotMatchException;
import com.skillbox.auth.exception.PasswordsDoesNotMatchException;
import com.skillbox.auth.exception.TokenRefreshException;
import com.skillbox.auth.security.AppUserDetails;
import com.skillbox.auth.service.CaptchaService;
import com.skillbox.auth.service.KafkaMessagePublisher;
import com.skillbox.auth.service.RefreshTokenService;
import com.skillbox.auth.service.UserService;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Base64;
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

        String imageId = UUID.randomUUID().toString();
        captchaService.saveImageToStorage(imageDirectory, imageId);
        CaptchaResponse data = new CaptchaResponse(imageAddress.concat(imageId), imageId);

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDtoRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();

        log.info("Calling registerUser");
        log.info(request.toString());
        if (!request.getCaptchaCode()
                .equals(captchaService.getCaptchaById(request.getCaptchaSecret()).getCaptchaCode())) {
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
    ResponseEntity<AuthenticationDtoResponse> authUser(@Valid @RequestBody LoginDtoRequest request) {
        RefreshTokenEntity refreshToken = null;
        String token = null;
        try {
            log.info("Начинаем аутентифицировать пользователя");
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

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ResponseEntity
                .ok(new AuthenticationDtoResponse(token, refreshToken.getToken()));
    }


    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String token = jwtTokenUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    private void sendUserRegisteredEvent(UserEntity user) {

        UserRegistrationEvent event = new UserRegistrationEvent();
        event.setUserRegistration(new UserRegistration(
                user.getId()
        ,user.getFirstName()
        ,user.getLastName()
        , user.getEmail()));
        try {
            kafkaMessagePublisher.sendUserRegistrationEvent(event);
            log.info("Message sent to kafka");
        } catch(KafkaException exception) {
            log.info("Message sent to kafka {}", exception.getMessage());
        }


    }

}
