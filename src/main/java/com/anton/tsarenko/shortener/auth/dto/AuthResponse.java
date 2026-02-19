package com.anton.tsarenko.shortener.auth.dto;

import java.io.Serializable;

/**
 * Record for response to auth requests.
 *
 * @param accessToken - token.
 */
public record AuthResponse(
        String accessToken
) implements Serializable {}
