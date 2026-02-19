package com.anton.tsarenko.shortener.url.mapper.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.dto.PageResponse;
import com.anton.tsarenko.shortener.url.dto.UrlRequest;
import com.anton.tsarenko.shortener.url.dto.UrlResponse;
import com.anton.tsarenko.shortener.url.entity.Url;
import com.anton.tsarenko.shortener.url.mapper.UrlMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Implementation of the UrlMapper interface for mapping between Url entities and DTOs.
 */
@Component
public class UrlMapperImpl implements UrlMapper {
    @Override
    public Url toUrl(UrlRequest urlRequest, User user) {
        return Url.builder()
                .originalUrl(urlRequest.originalUrl())
                .shortCode(urlRequest.shortCode())
                .user(user)
                .expiredAt(urlRequest.expiredAt())
                .build();
    }

    @Override
    public PageResponse<UrlResponse> toPageResponse(Page<Url> urlPage) {
        if (urlPage == null) {
            return null;
        }

        return new PageResponse<>(
                urlPage.map(this::toUrlResponse).getContent(),
                urlPage.getNumber(),
                urlPage.getSize(),
                urlPage.getTotalElements(),
                urlPage.getTotalPages()
        );
    }

    @Override
    public UrlResponse toUrlResponse(Url url) {
        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .redirectsCount(url.getRedirectsCount())
                .expiredAt(url.getExpiredAt())
                .createdAt(url.getCreatedAt())
                .build();
    }
}
