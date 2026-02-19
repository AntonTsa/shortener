package com.anton.tsarenko.shortener.url.service.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.entity.Url;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * This class is a fixture for testing {@link UrlServiceImpl}.
 */
class UrlServiceImplFixture {
    /** Valid user for service tests. */
    static final User VALID_USER = User.builder()
            .id(1L)
            .username("ValidUser11")
            .passwordHash("hash")
            .build();

    /** Pageable used in retrieval tests. */
    static final Pageable PAGEABLE = PageRequest.of(0, 2);

    /** URL entity used as input in create scenario. */
    static final Url URL_TO_CREATE = Url.builder()
            .user(VALID_USER)
            .originalUrl("https://example.com/create")
            .expiredAt(Instant.parse("2030-01-01T00:00:00Z"))
            .build();

    /** URL entity returned after save operation. */
    static final Url SAVED_URL = Url.builder()
            .id(15L)
            .user(VALID_USER)
            .originalUrl("https://example.com/create")
            .shortCode("abc123")
            .expiredAt(Instant.parse("2030-01-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();

    /** Expected created URL identifier. */
    static final Long CREATED_URL_ID = 15L;

    /** Sample URL page returned by repository. */
    static final Page<Url> URL_PAGE = new PageImpl<>(
            List.of(SAVED_URL),
            PAGEABLE,
            1
    );
}
