package com.skillbox.auth.service;

import com.skillbox.auth.entity.PasswordResetToken;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.exception.ResetPasswordTokenExpiredException;
import com.skillbox.auth.exception.ResetTokenNotFoundException;
import com.skillbox.auth.exception.TokenRefreshException;
import com.skillbox.auth.repository.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public void createPasswordResetToken(UserEntity user, String passwordToken) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(passwordToken);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpirationTime(Instant.now().plusMillis(300 * 1000));

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public UserEntity findByResetToken(String resetToken) {
        return passwordResetTokenRepository.findPasswordResetTokenByToken(resetToken)
                .orElseThrow(() -> new ResetTokenNotFoundException("Токен не найден"))
                .getUser();
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByToken(token)
                .orElseThrow();

        UserEntity user = passwordResetToken.getUser();
        log.info(String.valueOf(passwordResetToken.getExpirationTime().compareTo(Instant.now()) < 0));
        if(passwordResetToken.getExpirationTime().compareTo(Instant.now()) < 0) {

            passwordResetTokenRepository.delete(passwordResetToken);
            throw new ResetPasswordTokenExpiredException(passwordResetToken.getToken(), "Reset token was expired. Please make request again");
        }

        return true;
    }
}
