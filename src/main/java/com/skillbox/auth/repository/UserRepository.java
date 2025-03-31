package com.skillbox.auth.repository;

import com.skillbox.auth.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
}
