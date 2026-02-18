package com.anton.tsarenko.shortener.auth.dto;

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
public record ExceptionResponse(
        LocalDateTime timestamp,
        String error,
        String message
) implements Serializable {}
