package com.skillbox.auth.service;

import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.dto.request.PasswordResetRequest;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User with email " +
                        email + " not found")
        );
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserEntity saveUser(CreateUserDtoRequest request) {
        log.info("Calling saveUser");
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles( new String[] {"ROLE_USER"});
        user.setPassword(passwordEncoder.encode(request.getPassword1()));

        return userRepository.save(user);
    }

    @Override
    public void saveUser(PasswordResetRequest request, UserEntity user) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserEntity findUserById(String userId) {
        return userRepository.findById(userId).orElseThrow();
    }


}
