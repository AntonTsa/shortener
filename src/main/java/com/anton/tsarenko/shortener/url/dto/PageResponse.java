package com.anton.tsarenko.shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * A generic record to represent paginated responses.
 *
 * @param content       the list of items on the current page
 * @param page          the current page number (0-based)
 * @param size          the number of items per page
 * @param totalElements the total number of items across all pages
 * @param totalPages    the total number of pages available
 * @param <T>           the type of items in the content list
 */
@Schema(description = "Generic paginated response")
public record PageResponse<T>(
        @Schema(description = "Items on the current page")
        List<T> content,
        @Schema(description = "Current page index (0-based)", example = "0")
        int page,
        @Schema(description = "Page size", example = "10")
        int size,
        @Schema(description = "Total number of items", example = "42")
        long totalElements,
        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) implements Serializable {}
