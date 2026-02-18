package com.anton.tsarenko.shortener.auth.mapper.impl;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementation of the UserMapper interface.
 */
@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User toUser(AuthRequest request) {
        return User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
    }
}
