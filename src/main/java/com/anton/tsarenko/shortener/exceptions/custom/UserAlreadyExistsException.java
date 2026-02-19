package com.anton.tsarenko.shortener.exceptions.custom;

/**
 * Exception thrown when trying to create a user with a username that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param username - concrete username that already exists in the system.
     */
    public UserAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }
}
