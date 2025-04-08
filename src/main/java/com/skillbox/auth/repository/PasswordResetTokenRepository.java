package com.skillbox.auth.repository;

import com.skillbox.auth.entity.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findPasswordResetTokenByToken(String token);
}
