package com.anton.tsarenko.shortener.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
    private static final String TOKEN = "test-token";
    private static final String USERNAME = "TestUser12";

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

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
    void givenRequestWithoutAuthorization_whenFilterInvoked_thenContinuesChain() throws Exception {
        // GIVEN
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

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
    void givenRequestWithNonBearerHeader_whenFilterInvoked_thenContinuesChain() throws Exception {
        // GIVEN
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");
        MockHttpServletResponse response = new MockHttpServletResponse();

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
    void givenRequestWithInvalidToken_whenFilterInvoked_thenNoAuthenticationSet() throws Exception {
        // GIVEN
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
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
    void givenValidTokenAndEmptyContext_whenFilterInvoked_thenSetsAuthentication()
            throws Exception {
        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtService.isTokenValid(TOKEN)).willReturn(true);
        given(jwtService.extractUsername(TOKEN)).willReturn(USERNAME);

        // WHEN
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);
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
    void givenValidTokenAndExistingAuth_whenFilterInvoked_thenKeepsExistingAuthentication()
            throws Exception {
        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
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
        MockHttpServletResponse response = new MockHttpServletResponse();
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService).extractUsername(TOKEN);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(existingAuthentication);
    }
}
