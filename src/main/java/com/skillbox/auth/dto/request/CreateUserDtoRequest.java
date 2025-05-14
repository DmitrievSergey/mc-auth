package com.skillbox.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

@ToString
public class CreateUserDtoRequest {

    @NotNull
    @Size(min = 3, max = 50, message = "Имя пользователя не должно быть меньше {min} символов и не должно быть больше {max} символов")
    private String firstName;

    @NotNull
    @Size(min = 3, max = 50, message = "Фамилия пользователя не должно быть меньше {min} символов и не должно быть больше {max} символов")
    private String lastName;


    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String password1;

    @NotNull
    @Size(min = 8, max = 20, message = "Пароль пользователя не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String password2;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 3, max = 20, message = "Код не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String captchaCode;

    @NotNull
    @Size(min = 3, max = 50, message = "Код не должен быть меньше {min} символов и не должен быть больше {max} символов")
    private String captchaSecret;

    public CreateUserDtoRequest(String firstName, String lastName, String password1, String password2, String email, String captchaCode, String captchaSecret) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password1 = password1;
        this.password2 = password2;
        this.email = email;
        this.captchaCode = captchaCode;
        this.captchaSecret = captchaSecret;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    public String getEmail() {
        return email;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }
}
