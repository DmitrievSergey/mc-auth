package com.skillbox.auth.exception;

public class CaptchaDoesNotMatchException extends RuntimeException{
    public CaptchaDoesNotMatchException(String message) {
        super(message);
    }
}
