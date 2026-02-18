package com.anton.tsarenko.shortener.auth.dto;

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
public record AuthRequest(
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,32}$",
                message = "Username must be at least 8 characters long and "
                        + "contain at least one uppercase letter, one lowercase letter and "
                        + "one digit"
        )
        String username,
        @NotBlank
        @Size(min = 5, max = 32, message = "Password must be between 5 and 32 characters long")
        String password
) implements Serializable {
}
