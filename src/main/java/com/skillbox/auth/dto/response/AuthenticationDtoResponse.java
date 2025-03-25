package com.skillbox.auth.dto.response;

import com.skillbox.auth.entity.RefreshTokenEntity;

public class AuthenticationDtoResponse {
    private String accessToken;
    private String refreshToken;

    public AuthenticationDtoResponse(String token, String refreshToken) {
        this.accessToken = token;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
