package com.anton.tsarenko.shortener.url.service;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing URLs in the URL shortener application.
 */
public interface UrlService {
    /**
     * Creates a new URL entry in the system.
     *
     * @param url - The URL entity to be created.
     * @return - The ID of the newly created URL.
     */
    Long createUrl(Url url);

    /**
     * Retrieves all Urls associated with a specific user, with pagination support.
     *
     * @param user - The user whose URLs are to be retrieved.
     * @param pageable - The pagination information (page number, page size, sorting).
     * @return - A paginated list of Urls associated with the specified user.
     */
    Page<Url> retrieveAllUrlsByUser(User user, Pageable pageable);

    /**
     * Replaces a URL by its unique ID.
     *
     * @param id - The unique identifier of the URL to be retrieved.
     * @param url - The URL entity containing the updated information to replace the existing URL.
     */
    void replaceUrl(Long id, Url url);

    /**
     * Deletes a URL by its unique ID.
     *
     * @param id - The unique identifier of the URL to be deleted.
     */
    void deleteUrl(Long id);
}
