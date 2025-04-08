package com.skillbox.auth.exception;

public class PasswordsDoesNotMatchException extends RuntimeException{
    public PasswordsDoesNotMatchException(String message) {
        super(message);
    }
}
