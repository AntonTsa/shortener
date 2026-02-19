package com.anton.tsarenko.shortener.url.service.impl;

import static com.anton.tsarenko.shortener.url.util.ShortUrlGenerator.generateShortCode;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.exceptions.UrlNotFoundException;
import com.anton.tsarenko.shortener.url.entity.Url;
import com.anton.tsarenko.shortener.url.repo.UrlRepository;
import com.anton.tsarenko.shortener.url.service.UrlService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UrlService interface for handling CRUD Url operations.
 */
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;

    @Override
    public Long createUrl(Url url) {
        String shortCode = getNewShortCode();

        url.setShortCode(shortCode);
        return urlRepository.save(url).getId();
    }

    private String getNewShortCode() {
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (!urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    @Override
    public Page<Url> retrieveAllUrlsByUser(User user, Pageable pageable) {
        return urlRepository.findAllByUser(user, pageable);
    }

    @Override
    public void deleteUrl(Long id) {
        urlRepository.deleteById(id);
    }

    @Override
    @Transactional
    public String resolveOriginalUrlAndIncreaseRedirectCount(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(
                        "Url is not found by short code: " + shortCode
                ));

        long currentRedirects = url.getRedirectsCount() == null ? 0L : url.getRedirectsCount();
        url.setRedirectsCount(currentRedirects + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}
