package com.skillbox.auth.service;

import com.skillbox.auth.entity.PasswordResetToken;
import com.skillbox.auth.entity.UserEntity;

public interface PasswordResetService {
    void createPasswordResetToken(UserEntity user, String passwordToken);
    UserEntity findByResetToken(String resetToken);
    boolean validatePasswordResetToken(String token);
}
