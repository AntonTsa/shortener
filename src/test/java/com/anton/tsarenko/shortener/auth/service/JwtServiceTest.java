package com.anton.tsarenko.shortener.auth.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.shortener.PostgresTestContainer;
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
class JwtServiceTest extends PostgresTestContainer {
    private static final String SECRET_KEY = "test-test-test-test-test-test-test-test";
    private static final long EXPIRATION_IN_MINUTES = 30L;
    private static final String USERNAME = "TestUser12";

    private final JwtService jwtService = new JwtServiceImpl(SECRET_KEY, EXPIRATION_IN_MINUTES);

    @Test
    @DisplayName("""
            GIVEN valid username
            WHEN generateAccessToken is called
            THEN returns signed token with username and USER role claim
            """)
    void givenValidUsername_whenGenerateAccessToken_thenReturnsTokenWithClaims() {
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
            GIVEN generated token
            WHEN extractUsername is called
            THEN returns username from token
            """)
    void givenGeneratedToken_whenExtractUsername_thenReturnsUsername() {
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
    void givenValidToken_whenIsTokenValid_thenReturnsTrue() {
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
    void givenMalformedToken_whenIsTokenValid_thenReturnsFalse() {
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
    void givenTokenWithAnotherSecret_whenIsTokenValid_thenReturnsFalse() {
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
    void givenExpiredToken_whenIsTokenValid_thenReturnsFalse() {
        // GIVEN
        JwtService expiredJwtService = new JwtServiceImpl(SECRET_KEY, -1L);
        String expiredToken = expiredJwtService.generateAccessToken(USERNAME);

        // WHEN
        boolean actualResult = jwtService.isTokenValid(expiredToken);

        // THEN
        assertThat(actualResult).isFalse();
    }
}
