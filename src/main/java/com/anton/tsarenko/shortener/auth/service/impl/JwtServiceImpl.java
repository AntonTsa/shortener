package com.anton.tsarenko.shortener.auth.service.impl;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import com.anton.tsarenko.shortener.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Implementation of the JwtService interface for handling JWT operations.
 */
@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey key;
    private final long expirationInMinutes;

    /**
     * Constructs a JwtServiceImpl with the specified secret and access token TTL.
     *
     * @param secret           the secret key used for signing JWTs
     * @param expirationInMinutes the time-to-live for access tokens in minutes
     */
    public JwtServiceImpl(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long expirationInMinutes
    ) {
        this.key = hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        this.expirationInMinutes = expirationInMinutes;
    }

    @Override
    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Instant exp = now.plus(expirationInMinutes, java.time.temporal.ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", List.of("USER"))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
