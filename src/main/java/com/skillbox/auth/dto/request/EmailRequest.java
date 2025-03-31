package com.skillbox.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class EmailRequest {
    @NotNull
    @Email
    private String email;

    public String getEmail() {
        return email;
    }
}
