package com.anton.tsarenko.shortener.auth.service;

import com.anton.tsarenko.shortener.auth.entity.User;

/**
 * Service interface for handling authentication-related operations such as
 * user registration and login.
 */
public interface AuthService {

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param user user details
     */
    void register(User user);

    /**
     * Logins a new user based on the provided login request.
     *
     * @param user user details
     * @return login response
     */
    String login(User user);

}
