package com.anton.tsarenko.shortener.auth.service;

import static com.anton.tsarenko.shortener.auth.service.JwtServiceFixture.EXPIRATION_IN_MINUTES;
import static com.anton.tsarenko.shortener.auth.service.JwtServiceFixture.SECRET_KEY;
import static com.anton.tsarenko.shortener.auth.service.JwtServiceFixture.USERNAME;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.shortener.auth.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JwtServiceImpl}.
 */
class JwtServiceTest {

    private final JwtService jwtService = new JwtServiceImpl(SECRET_KEY, EXPIRATION_IN_MINUTES);

    @Test
    @DisplayName("""
            GIVEN valid username
            WHEN generateAccessToken is called
            THEN returns signed token with username and USER role claim
            """)
    void generateAccessTokenValid() {
        // GIVEN
        SecretKey key = hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        // WHEN
        String token = jwtService.generateAccessToken(USERNAME);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // THEN
        assertThat(token).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo(USERNAME);
        assertThat(claims.get("roles")).isEqualTo(java.util.List.of("USER"));
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("""
            GIVEN valid generated token
            WHEN extractUsername is called
            THEN returns username from token
            """)
    void extractUsernameValid() {
        // GIVEN
        String token = jwtService.generateAccessToken(USERNAME);

        // WHEN
        String extractedUsername = jwtService.extractUsername(token);

        // THEN
        assertThat(extractedUsername).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("""
            GIVEN valid token
            WHEN isTokenValid is called
            THEN returns true
            """)
    void isTokenValid_Valid() {
        // GIVEN
        String token = jwtService.generateAccessToken(USERNAME);

        // WHEN
        boolean actualResult = jwtService.isTokenValid(token);

        // THEN
        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("""
            GIVEN malformed token
            WHEN isTokenValid is called
            THEN returns false
            """)
    void isTokenValid_Malformed() {
        // GIVEN
        String malformedToken = "not-a-jwt-token";

        // WHEN
        boolean actualResult = jwtService.isTokenValid(malformedToken);

        // THEN
        assertThat(actualResult).isFalse();
    }

    @Test
    @DisplayName("""
            GIVEN token signed with another secret
            WHEN isTokenValid is called
            THEN returns false
            """)
    void isTokenValid_TokenWithAnotherSecret() {
        // GIVEN
        JwtService anotherJwtService = new JwtServiceImpl(
                "another-another-another-another-another12",
                EXPIRATION_IN_MINUTES);
        String tokenSignedByAnotherSecret = anotherJwtService.generateAccessToken(USERNAME);

        // WHEN
        boolean actualResult = jwtService.isTokenValid(tokenSignedByAnotherSecret);

        // THEN
        assertThat(actualResult).isFalse();
    }

    @Test
    @DisplayName("""
            GIVEN expired token
            WHEN isTokenValid is called
            THEN returns false
            """)
    void isTokenValid_ExpiredToken() {
        // GIVEN
        JwtService expiredJwtService = new JwtServiceImpl(SECRET_KEY, -1L);
        String expiredToken = expiredJwtService.generateAccessToken(USERNAME);

        // WHEN
        boolean actualResult = jwtService.isTokenValid(expiredToken);

        // THEN
        assertThat(actualResult).isFalse();
    }
}
