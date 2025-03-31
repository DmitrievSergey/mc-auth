package com.skillbox.auth.service;

import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.dto.request.PasswordResetRequest;
import com.skillbox.auth.entity.UserEntity;

public interface UserService {
    UserEntity saveUser(CreateUserDtoRequest request);
    void saveUser(PasswordResetRequest request, UserEntity user);
    UserEntity findUserById(String userId);
    UserEntity findUserByEmail(String email);

}
