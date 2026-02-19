package com.anton.tsarenko.shortener.url.controller;

import com.anton.tsarenko.shortener.url.service.UrlService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for redirecting by short code.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/V1/s_link")
public class RedirectController {
    private final UrlService urlService;

    /**
     * Resolves short code, increments redirects and performs redirect.
     *
     * @param shortCode short code value
     * @return redirect response with location header
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectByShortCode(
            @PathVariable String shortCode
    ) {
        String originalUrl = urlService.resolveOriginalUrlAndIncreaseRedirectCount(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
