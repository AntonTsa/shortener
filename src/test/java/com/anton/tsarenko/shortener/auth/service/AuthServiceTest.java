package com.anton.tsarenko.shortener.auth.service;

import static com.anton.tsarenko.shortener.auth.service.AuthServiceFixture.ACCESS_TOKEN_VALID;
import static com.anton.tsarenko.shortener.auth.service.AuthServiceFixture.AUTHENTICATION_VALID;
import static com.anton.tsarenko.shortener.auth.service.AuthServiceFixture.VALID_USER;
import static com.anton.tsarenko.shortener.auth.service.AuthServiceFixture.VALID_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.repo.UsersRepository;
import com.anton.tsarenko.shortener.auth.service.impl.AuthServiceImpl;
import com.anton.tsarenko.shortener.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

/**
 * Unit tests for {@link AuthServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("""
            GIVEN valid User object
            WHEN register is called
            THEN make a registration and returns nothing
            """)
    void registrationValid() {
        // GIVEN
        given(usersRepository.existsByUsername(VALID_USERNAME)).willReturn(false);
        given(usersRepository.save(VALID_USER)).willReturn(VALID_USER);

        // WHEN / THEN
        assertThatCode(() -> authService.register(VALID_USER)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("""
            GIVEN valid User object with username, that already exists
            WHEN register is called
            THEN throws UserAlreadyExistsException and does not save user
            """)
    void registrationExistingUsername() {
        // GIVEN
        given(usersRepository.existsByUsername(VALID_USERNAME)).willReturn(true);

        // WHEN AND THEN
        assertThatThrownBy(() -> authService.register(VALID_USER))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists: " + VALID_USERNAME);
        // THEN
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("""
            GIVEN valid User object
            WHEN login is called
            THEN returns auth response with access token
            """)
    void loginValid() {
        // GIVEN
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(AUTHENTICATION_VALID);
        given(jwtService.generateAccessToken(anyString())).willReturn(ACCESS_TOKEN_VALID);

        // WHEN
        String actualResponse = authService.login(VALID_USER);

        // THEN
        assertThat(actualResponse).isEqualTo(ACCESS_TOKEN_VALID);
    }

    @Test
    @DisplayName("""
            GIVEN authentication manager fails with any authentication exception
            WHEN login is called
            THEN translates it to BadCredentialsException
            """)
    void loginAuthenticationException() {
        // GIVEN
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new AuthenticationException("Auth failed") {
                });

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(VALID_USER))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }
}
