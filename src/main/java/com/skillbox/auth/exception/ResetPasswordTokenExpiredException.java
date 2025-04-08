package com.skillbox.auth.exception;

public class ResetPasswordTokenExpiredException extends RuntimeException{
    public ResetPasswordTokenExpiredException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
