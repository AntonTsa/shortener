package com.anton.tsarenko.shortener.url.controller;

import com.anton.tsarenko.shortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping({"/api/v1/s_link"})
public class RedirectController {
    private final UrlService urlService;

    /**
     * Resolves short code, increments redirects and performs redirect.
     *
     * @param shortCode short code value
     * @return redirect response with location header
     */
    @Operation(
            summary = "Redirect by short code",
            description = "Resolves a short code to the original URL and "
                    + "returns HTTP 302 with Location header."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302", description = "Redirect response with Location header"),
            @ApiResponse(responseCode = "404", description = "Short code not found")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectByShortCode(
            @Parameter(description = "Short code to resolve", example = "aB12xYz9")
            @PathVariable String shortCode
    ) {
        String originalUrl = urlService.resolveOriginalUrlAndIncreaseRedirectCount(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
