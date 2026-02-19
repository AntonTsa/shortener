package com.anton.tsarenko.shortener.url.mapper.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlRequest;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * This class is a fixture for testing {@link UrlMapperImpl}.
 */
class UrlMapperImplFixture {
    /** A valid user used for mapping tests. */
    static final User VALID_USER = User.builder()
            .id(1L)
            .username("ValidUser11")
            .passwordHash("hash")
            .build();

    /** A valid request DTO used for mapping tests. */
    static final UrlRequest VALID_URL_REQUEST = new UrlRequest(
            "https://example.com"
    );

    /** First URL entity in page mapping scenario. */
    static final Url FIRST_URL = Url.builder()
            .id(10L)
            .user(VALID_USER)
            .originalUrl("https://first.example.com")
            .shortCode("first1")
            .redirectsCount(5L)
            .expiredAt(Instant.parse("2030-02-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();

    /** Second URL entity in page mapping scenario. */
    static final Url SECOND_URL = Url.builder()
            .id(11L)
            .user(VALID_USER)
            .originalUrl("https://second.example.com")
            .shortCode("second2")
            .redirectsCount(2L)
            .expiredAt(Instant.parse("2030-03-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-02T00:00:00Z"))
            .build();

    /** Page object used in page mapping tests. */
    static final Page<Url> URL_PAGE = new PageImpl<>(
            List.of(FIRST_URL, SECOND_URL),
            PageRequest.of(1, 2),
            5
    );

    /** Expected mapped response for first URL entity. */
    static final UrlResponse FIRST_URL_RESPONSE = UrlResponse.builder()
            .id(10L)
            .originalUrl("https://first.example.com")
            .shortCode("first1")
            .redirectsCount(5L)
            .expiredAt(Instant.parse("2030-02-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();

    /** Expected mapped response for second URL entity. */
    static final UrlResponse SECOND_URL_RESPONSE = UrlResponse.builder()
            .id(11L)
            .originalUrl("https://second.example.com")
            .shortCode("second2")
            .redirectsCount(2L)
            .expiredAt(Instant.parse("2030-03-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-02T00:00:00Z"))
            .build();

    /** Expected paginated response for page mapping tests. */
    static final PageResponse<UrlResponse> EXPECTED_PAGE_RESPONSE = new PageResponse<>(
            List.of(FIRST_URL_RESPONSE, SECOND_URL_RESPONSE),
            1,
            2,
            5L,
            3
    );
}
