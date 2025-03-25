package com.skillbox.auth.service;

import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.entity.UserEntity;

public interface UserService {
    UserEntity saveUser(CreateUserDtoRequest request);
    UserEntity findUserById(String userId);
    UserEntity findUserByEmail(String email);

}
