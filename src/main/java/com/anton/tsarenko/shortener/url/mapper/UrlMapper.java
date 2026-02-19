package com.anton.tsarenko.shortener.url.mapper;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlRequest;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import org.springframework.data.domain.Page;

/**
 * Mapper interface for converting between Url entities and DTOs.
 */
public interface UrlMapper {
    /**
     * Converts a UrlRequest DTO to a Url entity.
     *
     * @param urlRequest - The UrlUpdateRequest DTO to convert
     * @param user - The User entity associated with the Url
     * @return - The corresponding Url entity
     */
    Url toUrl(UrlRequest urlRequest, User user);

    /**
     * converts a page of note entities to a page response of url responses.
     *
     * @param urls - The page of Url entities to convert
     * @return - The corresponding PageResponse containing UrlResponse DTOs
     */
    PageResponse<UrlResponse> toPageResponse(Page<Url> urls);

    /**
     * Converts a Url entity to a UrlResponse DTO.
     *
     * @param url - The Url entity to convert
     * @return - The corresponding UrlResponse DTO
     */
    UrlResponse toUrlResponse(Url url);
}
