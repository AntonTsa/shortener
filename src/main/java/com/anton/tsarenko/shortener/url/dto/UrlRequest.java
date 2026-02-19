package com.anton.tsarenko.shortener.url.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO class representing a request to create a new url.
 *
 * @param originalUrl - The original URL to be shortened.
 * @param shortCode - The short code to be used for the shortened URL.
 * @param expiredAt - The expiration time for the shortened URL.
 */
public record UrlRequest(
        String originalUrl,
        String shortCode,
        Instant expiredAt
) implements Serializable {}
