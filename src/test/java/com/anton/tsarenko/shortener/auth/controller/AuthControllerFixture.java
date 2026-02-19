package com.anton.tsarenko.shortener.auth.controller;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.entity.User;

/**
 * This class is a fixture for testing the AuthController. It is used to set up common test data
 * for testing authentication-related endpoints in the URL shortener application.
 */
class AuthControllerFixture {
    /** A valid AuthRequest object. */
    static final AuthRequest VALID_AUTH_REQUEST =
             new AuthRequest("testUser23", "password");
    /** A valid password hash that can be used in tests to simulate a successful authentication. */
    static final String VALID_PASSWORD_HASH =
            "$2a$10$7Qy8n1s5u9v6w3x2y1z0a9b8c7d6e5f4g3h2i1j0k9l8m7n6o5p4q3r2s1t0";
    /** A valid User object that can be used in tests to simulate a successful authentication. */
    static final User VALID_USER = User.builder()
            .id(1L)
            .username(VALID_AUTH_REQUEST.username())
            .passwordHash(VALID_PASSWORD_HASH)
            .build();
    /**
     * An invalid AuthRequest object that can be used in tests to simulate a failed authentication.
     */
    static final AuthRequest INVALID_AUTH_REQUEST =
             new AuthRequest("invalidUser", "passworditfrtdtdteedrwerwztrwws365duruytfsexr");

    /**
     * A message that can be used in tests to verify that the correct error message is returned when
     * a user tries to register with a weak password or invalid input.
     */
    static final String BAD_REQUEST_MESSAGE = "password: must be between 5 and 32 characters long,"
            + " username: must be at least 8 characters long and contain at least one uppercase "
            + "letter, one lowercase letter and one digit";

    /**
     * A username that already exists in the system, used to test scenarios where a user tries to
     * register with an existing username.
     */
    static final String EXISTED_USERNAME = "existedUser12";
    /**
     * An AuthRequest object with an existing username, used to test scenarios where a user tries to
     * register with an existing username.
     */
    static final AuthRequest EXISTED_USERNAME_AUTH_REQUEST =
            new AuthRequest(EXISTED_USERNAME, "password");
    /**
     * A User object representing an existing user with the existing username, used to test
     * scenarios where a user tries to register with an existing username.
     */
    static final User EXISTED_USERNAME_USER = User.builder()
            .id(2L)
            .username(EXISTED_USERNAME)
            .passwordHash(VALID_PASSWORD_HASH)
            .build();
    /**
     * A message that can be used in tests to verify that the correct error message is returned
     * when a user tries to register with an existing username.
     */
    static final String EXISTED_USERNAME_MESSAGE = "Username already exists: " + EXISTED_USERNAME;
    /**
     * A message that can be used in tests to verify that the correct error message is returned when
     * a user tries to log in with invalid credentials.
     */
    static final String BAD_CREDENTIALS_MESSAGE = "Invalid username or password";
    /** A valid token string that can be used in tests to simulate a successful authentication. */
    static final String VALID_TOKEN = "TestTokenValid";
    /**
     * A valid AuthResponse object that can be used in tests to simulate a successful authentication
     * response.
     */
    static final AuthResponse VALID_AUTH_RESPONSE = new AuthResponse(VALID_TOKEN);

    /**
     * The endpoint for user registration, used in tests to verify that the correct endpoint is
     * being called for registration-related operations.
     */
    static final String REGISTRATION_ENDPOINT = "/api/v1/auth/registration";

    /**
     * The endpoint for user login, used in tests to verify that the correct endpoint is
     * being called for login-related operations.
     */
    static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
}
