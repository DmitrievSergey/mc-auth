package com.skillbox.auth.repository;

import com.skillbox.auth.entity.RefreshTokenEntity;
import com.skillbox.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
    @Modifying
    int deleteByUser(UserEntity user);
}
