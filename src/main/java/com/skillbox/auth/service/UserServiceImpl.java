package com.skillbox.auth.service;

import com.skillbox.auth.dto.request.ChangePasswordRequest;
import com.skillbox.auth.dto.request.CreateUserDtoRequest;
import com.skillbox.auth.dto.request.PasswordResetRequest;
import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.exception.PasswordsDoesNotMatchException;
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
    //TODO: поймать exception прокинуть дальше emailAlreadyExist
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    @Override
    public void saveUser(ChangePasswordRequest request, UserEntity user) {
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new PasswordsDoesNotMatchException("Указан неправильный пароль");
        }

        if(!request.getNewPassword1().equals(request.getNewPassword2())) {
            throw new PasswordsDoesNotMatchException("Пароли не совпадают");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword1()));
        userRepository.save(user);
    }

    @Override
    public UserEntity findUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(

        );
    }


}
