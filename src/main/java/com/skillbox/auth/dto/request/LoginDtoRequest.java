package com.skillbox.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginDtoRequest {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String password;

    public LoginDtoRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
