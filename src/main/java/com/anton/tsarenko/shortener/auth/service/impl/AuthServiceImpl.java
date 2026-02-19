package com.anton.tsarenko.shortener.auth.service.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
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
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public void register(User user) {
        if (usersRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        usersRepository.save(user);
    }

    @Override
    public String login(User user) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPasswordHash())
            );

            return
                    jwtService.generateAccessToken(authentication.getName());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
