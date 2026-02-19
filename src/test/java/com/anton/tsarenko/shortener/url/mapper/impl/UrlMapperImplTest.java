package com.anton.tsarenko.shortener.url.mapper.impl;

import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.EXPECTED_PAGE_RESPONSE;
import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.FIRST_URL;
import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.FIRST_URL_RESPONSE;
import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.URL_PAGE;
import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.VALID_URL_REQUEST;
import static com.anton.tsarenko.shortener.url.mapper.impl.UrlMapperImplFixture.VALID_USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UrlMapperImpl}.
 */
class UrlMapperImplTest {

    private final UrlMapperImpl mapper = new UrlMapperImpl();

    @Test
    @DisplayName("""
            GIVEN valid UrlRequest and user
            WHEN toUrl is called
            THEN maps request fields and user to Url entity
            """)
    void toUrlValid() {
        // GIVEN

        // WHEN
        Url actualUrl = mapper.toUrl(VALID_URL_REQUEST, VALID_USER);

        // THEN
        assertThat(actualUrl.getUser()).isEqualTo(VALID_USER);
        assertThat(actualUrl.getOriginalUrl()).isEqualTo(VALID_URL_REQUEST.originalUrl());
        assertThat(actualUrl.getExpiredAt()).isEqualTo(VALID_URL_REQUEST.expiredAt());
    }

    @Test
    @DisplayName("""
            GIVEN null page
            WHEN toPageResponse is called
            THEN returns null
            """)
    void toPageResponseNullPage() {
        // GIVEN

        // WHEN
        PageResponse<UrlResponse> actualResponse = mapper.toPageResponse(null);

        // THEN
        assertThat(actualResponse).isNull();
    }

    @Test
    @DisplayName("""
            GIVEN page with url entities
            WHEN toPageResponse is called
            THEN maps content and pagination metadata
            """)
    void toPageResponseValidPage() {
        // GIVEN

        // WHEN
        PageResponse<UrlResponse> actualResponse = mapper.toPageResponse(URL_PAGE);

        // THEN
        assertThat(actualResponse).isEqualTo(EXPECTED_PAGE_RESPONSE);
    }

    @Test
    @DisplayName("""
            GIVEN valid Url entity
            WHEN toUrlResponse is called
            THEN maps url fields to UrlResponse
            """)
    void toUrlResponseValid() {
        // GIVEN

        // WHEN
        UrlResponse actualResponse = mapper.toUrlResponse(FIRST_URL);

        // THEN
        assertThat(actualResponse).isEqualTo(FIRST_URL_RESPONSE);
    }
}
