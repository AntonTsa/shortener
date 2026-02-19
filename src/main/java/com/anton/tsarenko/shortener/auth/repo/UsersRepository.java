package com.anton.tsarenko.shortener.auth.repo;

import com.anton.tsarenko.shortener.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing User entities.
 */
public interface UsersRepository extends JpaRepository<User, Long> {
    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
