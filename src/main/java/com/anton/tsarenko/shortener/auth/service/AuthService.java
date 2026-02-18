package com.anton.tsarenko.shortener.auth.service;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;

/**
 * Service interface for handling authentication-related operations such as
 * user registration and login.
 */
public interface AuthService {

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param request the registration request containing user details
     */
    void register(AuthRequest request);

    /**
     * Logins a new user based on the provided login request.
     *
     * @param request the login request containing user details
     * @return login response
     */
    AuthResponse login(AuthRequest request);

}
