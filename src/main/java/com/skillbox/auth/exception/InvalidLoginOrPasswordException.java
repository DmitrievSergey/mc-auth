package com.skillbox.auth.exception;

public class InvalidLoginOrPasswordException extends RuntimeException {
    public InvalidLoginOrPasswordException(String message) {
        super(message);
    }
}
