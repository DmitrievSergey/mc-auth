package com.skillbox.auth.security;

import com.skillbox.auth.entity.UserEntity;
import com.skillbox.auth.repository.UserRepository;
import com.skillbox.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with username: " + username)
        );

        return AppUserDetails.build(user);
    }
}
