package com.anton.tsarenko.shortener.url.service.impl;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.url.entity.Url;
import com.anton.tsarenko.shortener.url.repo.UrlRepository;
import com.anton.tsarenko.shortener.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Implementation of the UrlService interface for handling CRUD Url operations.
 */
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    @Override
    public Long createUrl(Url url) {
        return urlRepository.save(url).getId();
    }

    @Override
    public Page<Url> retrieveAllUrlsByUser(User user, Pageable pageable) {
        return urlRepository.findAllByUser(user, pageable);
    }

    @Override
    public void replaceUrl(Long id, Url url) {
        if (!urlRepository.existsById(id)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        urlRepository.save(url);
    }

    @Override
    public void deleteUrl(Long id) {
        urlRepository.deleteById(id);
    }
}
