package com.anton.tsarenko.shortener.url.dto;

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
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) implements Serializable {}
