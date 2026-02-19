package com.anton.tsarenko.shortener.config;

import static com.anton.tsarenko.shortener.config.JwtAuthFilterFixture.TOKEN;
import static com.anton.tsarenko.shortener.config.JwtAuthFilterFixture.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit tests for {@link JwtAuthFilter}.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        filter = new JwtAuthFilter(jwtService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("""
            GIVEN request without Authorization header
            WHEN filter is invoked
            THEN it continues chain without JWT processing
            """)
    void filterWithoutAuthorizationHeader() throws Exception {
        // GIVEN
        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(TOKEN);
        verify(jwtService, never()).extractUsername(TOKEN);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("""
            GIVEN request with non-bearer Authorization header
            WHEN filter is invoked
            THEN it continues chain without JWT processing
            """)
    void filterNonBearerAuthorizationHeader() throws Exception {
        // GIVEN
        request.addHeader("Authorization", "Basic abc123");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).isTokenValid(TOKEN);
        verify(jwtService, never()).extractUsername(TOKEN);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("""
            GIVEN request with invalid bearer token
            WHEN filter is invoked
            THEN it continues chain and does not authenticate user
            """)
    void filterInvalidToken() throws Exception {
        // GIVEN
        request.addHeader("Authorization", "Bearer " + TOKEN);
        given(jwtService.isTokenValid(TOKEN)).willReturn(false);

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService, never()).extractUsername(TOKEN);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("""
            GIVEN request with valid bearer token and empty security context
            WHEN filter is invoked
            THEN it authenticates user with ROLE_USER and continues chain
            """)
    void filterValidTokenAndEmptyContext() throws Exception {
        // GIVEN
        request.addHeader("Authorization", "Bearer " + TOKEN);

        given(jwtService.isTokenValid(TOKEN)).willReturn(true);
        given(jwtService.extractUsername(TOKEN)).willReturn(USERNAME);

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService).extractUsername(TOKEN);
        verify(filterChain).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(USERNAME);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("""
            GIVEN request with valid bearer token and existing authentication
            WHEN filter is invoked
            THEN it keeps existing authentication and continues chain
            """)
    void filterValidTokenAndExistingAuthentication() throws Exception {
        // GIVEN
        request.addHeader("Authorization", "Bearer " + TOKEN);

        var existingAuthentication = new UsernamePasswordAuthenticationToken(
                "existingUser",
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        given(jwtService.isTokenValid(TOKEN)).willReturn(true);
        given(jwtService.extractUsername(TOKEN)).willReturn(USERNAME);

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService).extractUsername(TOKEN);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(existingAuthentication);
    }
}
