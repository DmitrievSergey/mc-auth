package com.skillbox.auth.service;

import com.skillbox.auth.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshTokenEntity> findByToken(String token);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    RefreshTokenEntity createRefreshToken(String userId);
}
