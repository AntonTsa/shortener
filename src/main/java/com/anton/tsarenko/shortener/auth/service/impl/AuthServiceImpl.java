package com.anton.tsarenko.shortener.auth.service.impl;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.mapper.UserMapper;
import com.anton.tsarenko.shortener.auth.repo.UsersRepository;
import com.anton.tsarenko.shortener.auth.service.AuthService;
import com.anton.tsarenko.shortener.auth.service.JwtService;
import com.anton.tsarenko.shortener.exceptions.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the authentication service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public void register(AuthRequest request) {
        if (usersRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = userMapper.toUser(request);

        usersRepository.save(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password())
            );

            return new AuthResponse(
                    jwtService.generateAccessToken(authentication.getName()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
