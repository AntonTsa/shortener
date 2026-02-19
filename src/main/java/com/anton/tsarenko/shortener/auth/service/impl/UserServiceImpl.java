package com.anton.tsarenko.shortener.auth.service.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.repo.UsersRepository;
import com.anton.tsarenko.shortener.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UserService interface for handling user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;

    /**
     * Retrieves a user by their id.
     *
     * @param id the id of the user to retrieve
     * @return the User object if found, or null if not found
     */
    public User getUserById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }
}
