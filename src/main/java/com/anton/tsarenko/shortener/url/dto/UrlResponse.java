package com.anton.tsarenko.shortener.url.dto;

import com.anton.tsarenko.shortener.auth.entity.User;
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
public record UrlResponse(
        Long id,
        User user,
        String originalUrl,
        Long redirectsCount,
        String shortCode,
        Instant expiredAt,
        Instant createdAt
) implements Serializable {}
