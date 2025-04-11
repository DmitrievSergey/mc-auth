package com.skillbox.auth.dto.response;


import java.sql.Timestamp;
import java.util.Date;

public class AuthenticationDtoResponse {
    private String accessToken;
    private String refreshToken;

    public AuthenticationDtoResponse() {
    }

    public AuthenticationDtoResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginDataResponse{");
        sb.append("accessToken='").append(accessToken).append('\'');
        sb.append(", refreshToken='").append(refreshToken).append('\'');
        sb.append('}');
        return sb.toString();
    }



}
