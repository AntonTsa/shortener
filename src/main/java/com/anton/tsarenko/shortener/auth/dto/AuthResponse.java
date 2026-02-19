package com.anton.tsarenko.shortener.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * Record for response to auth requests.
 *
 * @param accessToken - token.
 */
@Schema(description = "Authentication response payload")
public record AuthResponse(
        @Schema(
                description = "JWT access token",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huRG9lMSJ9.signature"
        )
        String accessToken
) implements Serializable {}
