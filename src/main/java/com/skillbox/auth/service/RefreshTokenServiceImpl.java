package com.skillbox.auth.service;

import com.skillbox.auth.entity.RefreshTokenEntity;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.exception.TokenRefreshException;
import com.skillbox.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jwt.refreshToken.lifetime}")
    private Long refreshTokenLifeTime;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    @Override
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    @Override
    public RefreshTokenEntity createRefreshToken(String userId) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUser(userService.findUserById(userId));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenLifeTime));
        refreshToken.setToken(UUID.randomUUID().toString().replace("-", ""));

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteToken(UserEntity user) {
        log.info("Зашли в delete token");
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(String userId) {
        return refreshTokenRepository.deleteByUser(userService.findUserById(userId));
    }
}
