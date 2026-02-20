package com.anton.tsarenko.shortener.auth.controller;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.mapper.UserMapper;
import com.anton.tsarenko.shortener.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * authorization.
 */
@Tag(name = "Auth", description = "Endpoints for user registration and authorization")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper mapper;

    /**
     * Endpoint for user registration.
     *
     * @param authRequest the registration request containing user details.
     * @return a response entity indicating the result of the registration process
     */
    @Operation(
            summary = "Registration",
            description = "Registers a new user with the provided username and password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Weak password / invalid input"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/registration")
    public ResponseEntity<Void> register(
            @RequestBody @Valid AuthRequest authRequest
    ) {
        User user = mapper.toUserForRegistration(authRequest);

        authService.register(user);

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
    @Operation(
            summary = "Login",
            description = "Authenticates a user with the provided username and password, "
                    + "returning a JWT token if successful.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token returned",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        User user = mapper.toUserForLogin(request);

        AuthResponse authResponse = new AuthResponse(authService.login(user));

        return ResponseEntity.ok(authResponse);
    }
}
