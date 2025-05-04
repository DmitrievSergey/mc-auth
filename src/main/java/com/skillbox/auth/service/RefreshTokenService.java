package com.skillbox.auth.service;

import com.skillbox.auth.entity.RefreshTokenEntity;
import com.skillbox.auth.entity.UserEntity;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshTokenEntity> findByToken(String token);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    RefreshTokenEntity createRefreshToken(String userId);
    void deleteToken(UserEntity user);
}
