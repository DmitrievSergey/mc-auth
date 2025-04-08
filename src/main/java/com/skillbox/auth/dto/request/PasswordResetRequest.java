package com.skillbox.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {

    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String password;

    public String getPassword() {
        return password;
    }
}
