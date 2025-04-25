package com.skillbox.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String oldPassword;

    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String newPassword1;

    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String newPassword2;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }
}
