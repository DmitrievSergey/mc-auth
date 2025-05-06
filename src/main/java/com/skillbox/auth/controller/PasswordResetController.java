package com.skillbox.auth.controller;

import com.skillbox.auth.dto.MailBody;
import com.skillbox.auth.dto.events.ResetPassword;
import com.skillbox.auth.dto.events.ResetPasswordEvent;
import com.skillbox.auth.dto.request.EmailRequest;
import com.skillbox.auth.dto.request.PasswordResetRequest;
import com.skillbox.auth.entity.PasswordResetToken;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.service.EmailService;
import com.skillbox.auth.service.KafkaMessagePublisher;
import com.skillbox.auth.service.PasswordResetService;
import com.skillbox.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class PasswordResetController {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    public PasswordResetController(UserService userService, EmailService emailService, PasswordResetService passwordResetService, KafkaMessagePublisher kafkaMessagePublisher) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @PostMapping("/password/recovery/")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailRequest request
            , HttpServletRequest servletRequest) {

        UserEntity user = userService.findUserByEmail(request.getEmail());
        log.info("Email - {}", request.getEmail());
        log.info("First name {}", user.getFirstName());
        String token = resetTokenGenerator();
        String linkForPasswordReset = generateEmailUrl(token, servletRequest);
        MailBody mailBody = new MailBody(
                request.getEmail()
                , "Your link for reset password"
                , linkForPasswordReset);

        PasswordResetToken resetToken = passwordResetService.createPasswordResetToken(user, token);

        emailService.sendSimpleMessage(mailBody);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/recovery/{resetToken}")
    public ResponseEntity<String> validateResetToken(@PathVariable String resetToken
            , @Valid @RequestBody PasswordResetRequest request) {

        UserEntity user = passwordResetService.findByResetToken(resetToken);
        boolean isValid = passwordResetService.validatePasswordResetToken(resetToken);
        if(isValid) {

            userService.saveUser(request, user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }


    }


    private String generateEmailUrl(String resetToken, HttpServletRequest request) {
        return getAppUrl(request).concat(resetToken);
    }

    private String resetTokenGenerator () {
        return UUID.randomUUID().toString().replace("-", "");
    }


    private String getAppUrl(HttpServletRequest request) {
        return "http://185.129.146.54:80/change-password/";
    }

}
