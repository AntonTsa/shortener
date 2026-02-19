package com.anton.tsarenko.shortener.auth.service;

import com.anton.tsarenko.shortener.auth.entity.User;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {
    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the User object corresponding to the provided id
     */
    User getUserById(Long id);
}
