package com.anton.tsarenko.shortener.exceptions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;

/**
 * A record representing a response with error.
 *
 * @param timestamp - current date and time when error occurs.
 * @param error - error that occurs.
 * @param message - message to the user.
 */
@Builder
@Schema(description = "Standard error response")
public record ExceptionResponse(
        @Schema(
                description = "Timestamp when the error occurred",
                example = "2026-02-19T19:30:00"
        )
        LocalDateTime timestamp,
        @Schema(
                description = "HTTP error title",
                example = "Bad Request"
        )
        String error,
        @Schema(
                description = "Detailed error message",
                example = "Username already exists"
        )
        String message
) implements Serializable {}
