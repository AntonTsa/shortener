package com.anton.tsarenko.shortener.url.dto;

import com.anton.tsarenko.shortener.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

/**
 * DTO class representing a response containing url information.
 *
 * @param id - the id of the url
 * @param user - the user who created the url
 * @param originalUrl - the original url
 * @param redirectsCount - the number of redirects for the url
 * @param shortCode - the short code for the url
 * @param expiredAt - the expiration date of the url
 * @param createdAt - the creation date of the url
 */
@Builder
@Schema(description = "Response payload with short URL details")
public record UrlResponse(
        @Schema(description = "URL record id", example = "1")
        Long id,
        @Schema(description = "Owner of this URL")
        User user,
        @Schema(description = "Original long URL", example = "https://example.com/some/long/path")
        String originalUrl,
        @Schema(description = "Number of redirects", example = "0")
        Long redirectsCount,
        @Schema(description = "Generated short code", example = "aB12xYz9")
        String shortCode,
        @Schema(
                description = "Expiration timestamp in UTC ISO-8601 format",
                example = "2030-01-01T00:00:00Z")
        Instant expiredAt,
        @Schema(
                description = "Creation timestamp in UTC ISO-8601 format",
                example = "2026-02-19T19:30:00Z")
        Instant createdAt
) implements Serializable {}
