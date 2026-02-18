package com.anton.tsarenko.shortener.auth.controller;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related HTTP requests, such as user registration and
 * login.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Endpoint for user registration.
     *
     * @param authRequest the registration request containing user details.
     * @return a response entity indicating the result of the registration process
     */
    @PostMapping("/registration")
    public ResponseEntity<Void> register(
            @RequestBody @Valid AuthRequest authRequest
    ) {
        authService.register(authRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Endpoint for user login.
     *
     * @param request request, containing user details.
     * @return a response entity indicating the result of the login process
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
