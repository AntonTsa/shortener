package com.anton.tsarenko.shortener.url.controller;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.service.UserService;
import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlRequest;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import com.anton.tsarenko.shortener.url.mapper.UrlMapper;
import com.anton.tsarenko.shortener.url.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling URL-related HTTP requests.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shortener/{userId}/links")
public class UrlController {
    private final UrlService urlService;
    private final UserService userService;
    private final UrlMapper mapper;

    /**
     * Handles the creation of a new URL.
     *
     * @param userId              the ID of the user creating the URL
     * @param urlRequest          the request body containing URL details
     * @param httpServletRequest  the HTTP servlet request for building the URI
     * @return a ResponseEntity containing the URI of the created URL
     */
    @PostMapping
    public ResponseEntity<URI> create(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody UrlRequest urlRequest,
            HttpServletRequest httpServletRequest
    ) {
        User user = userService.getUserById(userId);
        Url url = mapper.toUrl(urlRequest, user);
        return ResponseEntity.created(
                URI.create(
                        httpServletRequest.getRequestURI()
                                + "/"
                                + urlService.createUrl(url)
                )
        ).build();
    }

    /**
     * Retrieves URLs by ID of the user.
     *
     * @param userId - The ID of the user to whom the URL belongs
     * @param pageable -Tthe pagination and sorting information
     * @return a ResponseEntity containing the URL details
     */
    @GetMapping
    public ResponseEntity<PageResponse<UrlResponse>> getAllUrls(
            @PathVariable @Positive Long userId,
            @PageableDefault(size = 3)
            @SortDefault.SortDefaults(
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            ) Pageable pageable
    ) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(
                mapper.toPageResponse(urlService.retrieveAllUrlsByUser(user, pageable)));
    }

    /**
     * Updates an existing URL.
     *
     * @param userId     the ID of the user to whom the URL belongs
     * @param id         the ID of the URL to update
     * @param urlRequest the request body containing updated URL details
     * @return a ResponseEntity with no content
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUrl(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive long id,
            @Valid @RequestBody UrlRequest urlRequest
    ) {
        User user = userService.getUserById(userId);
        urlService.replaceUrl(id, mapper.toUrl(urlRequest, user));
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a URL by its ID.
     *
     * @param userId the ID of the user to whom the URL belongs
     * @param id     the ID of the URL to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long id
    ) {
        User user = userService.getUserById(userId);
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }
}
