package com.anton.tsarenko.shortener.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A record representing an authentication request for a user.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
@Schema(description = "Authentication request payload")
public record AuthRequest(
        @Schema(
                description = "Username for sign-up/sign-in",
                example = "JohnDoe1"
        )
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,32}$",
                message = "must be at least 8 characters long and "
                        + "contain at least one uppercase letter, one lowercase letter and "
                        + "one digit"
        )
        String username,
        @Schema(
                description = "Raw user password",
                example = "StrongPass123"
        )
        @NotBlank
        @Size(min = 5, max = 32, message = "must be between 5 and 32 characters long")
        String password
) implements Serializable {
}
