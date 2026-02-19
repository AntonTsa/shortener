package com.anton.tsarenko.shortener.url.controller;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlRequest;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * This class is a fixture for testing the UrlController.
 */
class UrlControllerFixture {
    /** Authorization header name. */
    static final String AUTHORIZATION_HEADER = "Authorization";

    /** Prefix for bearer token values. */
    static final String BEARER_PREFIX = "Bearer ";

    /** Valid token used for authenticated requests. */
    static final String VALID_TOKEN = "valid-url-controller-token";

    /** Valid username extracted from token. */
    static final String VALID_USERNAME = "ValidUser11";

    /** User ID used in requests. */
    static final Long USER_ID = 1L;

    /** URL ID used in requests and responses. */
    static final Long URL_ID = 10L;

    /** Endpoint for URL collection operations. */
    static final String LINKS_ENDPOINT = "/api/v1/shortener/" + USER_ID + "/links";

    /** Endpoint for URL item operations. */
    static final String LINK_BY_ID_ENDPOINT = LINKS_ENDPOINT + "/" + URL_ID;

    /** A valid user object used by controller tests. */
    static final User VALID_USER = User.builder()
            .id(USER_ID)
            .username(VALID_USERNAME)
            .passwordHash("hash")
            .build();

    /** A valid URL create request. */
    static final UrlRequest VALID_URL_REQUEST = new UrlRequest(
            "https://example.com/create",
            Instant.parse("2030-01-01T00:00:00Z")
    );

    /** URL entity mapped from create request. */
    static final Url URL_TO_CREATE = Url.builder()
            .user(VALID_USER)
            .originalUrl(VALID_URL_REQUEST.originalUrl())
            .expiredAt(VALID_URL_REQUEST.expiredAt())
            .build();

    /** URL entity returned by service in paged response. */
    static final Url STORED_URL = Url.builder()
            .id(URL_ID)
            .user(VALID_USER)
            .originalUrl("https://example.com/stored")
            .shortCode("stored10")
            .redirectsCount(3L)
            .expiredAt(Instant.parse("2030-03-01T00:00:00Z"))
            .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
            .build();

    /** Page of URL entities used in get all test. */
    static final Page<Url> URL_PAGE = new PageImpl<>(List.of(STORED_URL));

    /** URL response DTO used in get all test. */
    static final UrlResponse URL_RESPONSE = UrlResponse.builder()
            .id(URL_ID)
            .originalUrl(STORED_URL.getOriginalUrl())
            .shortCode(STORED_URL.getShortCode())
            .redirectsCount(STORED_URL.getRedirectsCount())
            .expiredAt(STORED_URL.getExpiredAt())
            .createdAt(STORED_URL.getCreatedAt())
            .build();

    /** Expected paginated URL response for get all endpoint. */
    static final PageResponse<UrlResponse> PAGE_RESPONSE = new PageResponse<>(
            List.of(URL_RESPONSE),
            0,
            1,
            1,
            1
    );
}
