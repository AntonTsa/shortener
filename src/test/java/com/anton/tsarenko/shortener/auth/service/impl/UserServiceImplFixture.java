package com.anton.tsarenko.shortener.auth.service.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import java.time.Instant;

/**
 * Fixture for {@link UserServiceImpl} tests.
 */
class UserServiceImplFixture {
    /** Existing user ID for service tests. */
    static final Long USER_ID = 1L;

    /** Existing user entity for service tests. */
    static final User EXISTING_USER = User.builder()
            .id(USER_ID)
            .username("valid-user")
            .passwordHash("hash")
            .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();
}
