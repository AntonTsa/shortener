package com.anton.tsarenko.shortener.auth.mapper;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.entity.User;

/**
 * Mapper interface for user-related operations.
 */
public interface UserMapper {
    /**
     * Converts AuthRequest DTO to User entity for registration flow.
     * Password is encoded before persisting.
     *
     * @param request the registration request containing user details
     * @return a User entity created from the registration request
     */
    User toUserForRegistration(AuthRequest request);

    /**
     * Converts AuthRequest DTO to User entity for login flow.
     * Password is kept raw and is validated by Spring Security against stored hash.
     *
     * @param request the login request containing user credentials
     * @return a User entity with raw password in passwordHash field as credential carrier
     */
    User toUserForLogin(AuthRequest request);
}
