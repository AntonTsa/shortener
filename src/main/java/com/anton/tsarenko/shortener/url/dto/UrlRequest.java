package com.anton.tsarenko.shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.hibernate.validator.constraints.URL;

/**
 * DTO class representing a request to update an existing url.
 *
 * @param originalUrl the original URL to be shortened
 */
@Schema(description = "Request payload for creating a short URL")
public record UrlRequest(
        @Schema(
                description = "Original URL to shorten",
                example = "https://example.com/some/long/path"
        )
        @URL(message = "Must be a valid URL")
        @NotBlank(message = "Must not be blank")
        @Size(max = 2048, message = "Length must be less than or equal to 2048 characters")
        String originalUrl
) implements Serializable {
}
