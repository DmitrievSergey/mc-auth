package com.skillbox.auth.controller;

import com.skillbox.auth.dto.response.ErrorResponse;
import com.skillbox.auth.exception.CaptchaDoesNotMatchException;
import com.skillbox.auth.exception.PasswordsDoesNotMatchException;
import com.skillbox.auth.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionHandlerController {
    ProblemDetail errorDetail = null;

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException exception) {
        return new ResponseEntity<> (new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CaptchaDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handleCaptcha(CaptchaDoesNotMatchException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value()
                , exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordsDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswords(PasswordsDoesNotMatchException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value()
                , exception.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityExceptions(Exception exception) {
        if(exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("access_denied_reason", "Not_authorize");
        }

        if(exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("access_denied_reason", "Bad_credentials");
        }

        if(exception != null) {
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("unknown_thing_reason", "Bad_credentials");
        }

        return errorDetail;
    }
}
