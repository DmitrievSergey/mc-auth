package com.skillbox.auth.exception;

public class ResetTokenNotFoundException extends RuntimeException {
    public ResetTokenNotFoundException(String message) {
        super(message);
    }
}
