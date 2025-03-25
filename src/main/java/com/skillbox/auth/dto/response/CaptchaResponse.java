package com.skillbox.auth.dto.response;

import java.util.UUID;

public class CaptchaResponse {
    private String image;
    private String secret;

    public CaptchaResponse(String image, String secret) {
        this.image = image;
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public String getImage() {
        return image;
    }
}
