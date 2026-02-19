package com.anton.tsarenko.shortener.auth.service;

import com.anton.tsarenko.shortener.auth.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * This class serves as a fixture for testing the AuthRequest class.
 * It provides sample data to facilitate testing of the AuthRequest.
 */
public class AuthServiceFixture {
    /** A valid username. */
    static final String VALID_USERNAME = "TestUser12";
    /** A valid password. */
    static final String VALID_PASSWORD_HASH = "password_hash";
    /** A valid User object that can be used in tests to simulate a successful authentication. */
    static final User VALID_USER = User.builder()
            .id(1L)
            .username(VALID_USERNAME)
            .passwordHash(VALID_PASSWORD_HASH)
            .build();
    /** An invalid username that is too short. */
    static final String ACCESS_TOKEN_VALID = "access-token";
    /** A valid Authentication object. */
    static final Authentication AUTHENTICATION_VALID =
            new UsernamePasswordAuthenticationToken(VALID_USER, VALID_PASSWORD_HASH);
}
