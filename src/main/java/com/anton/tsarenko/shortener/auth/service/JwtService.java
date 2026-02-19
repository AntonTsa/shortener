package com.anton.tsarenko.shortener.auth.service;

/**
 * Service interface for handling JWT (JSON Web Token) operations such as generating access tokens,
 * extracting usernames from tokens, and validating tokens.
 */
public interface JwtService {
    /**
     * Generates a JWT access token for the given username.
     *
     * @param username the username for which to generate the token
     * @return a JWT access token as a String
     */
    String generateAccessToken(String username);

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return the username extracted from the token
     */
    String extractUsername(String token);

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isTokenValid(String token);
}
