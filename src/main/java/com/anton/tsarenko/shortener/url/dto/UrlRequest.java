package com.anton.tsarenko.shortener.url.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO class representing a request to update an existing url.
 *
 * @param originalUrl the original URL to be shortened
 * @param expiredAt the expiration time for the shortened URL
 */
public record UrlRequest(
        String originalUrl,
        Instant expiredAt
) implements Serializable {
}
