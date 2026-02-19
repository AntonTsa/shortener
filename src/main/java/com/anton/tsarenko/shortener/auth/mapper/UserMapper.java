package com.anton.tsarenko.shortener.auth.mapper;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.entity.User;

/**
 * Mapper interface for user-related operations.
 */
public interface UserMapper {
    /**
     * Converts a AuthRequest DTO to a User entity.
     *
     * @param request the registration request containing user details
     * @return a User entity created from the registration request
     */
    User toUser(AuthRequest request);
}
