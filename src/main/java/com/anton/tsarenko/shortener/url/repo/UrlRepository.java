package com.anton.tsarenko.shortener.url.repo;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing URL entities.
 */
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Find all urls by user.
     *
     * @param user - The user whose URLs are to be retrieved.
     * @param pageable -  The pagination information (page number, page size, sorting).
     * @return - A paginated list of Urls associated with the specified user.
     */
    Page<Url> findAllByUser(User user, Pageable pageable);
}
