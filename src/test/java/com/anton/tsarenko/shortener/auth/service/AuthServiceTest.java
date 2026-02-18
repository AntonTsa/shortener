package com.anton.tsarenko.shortener.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.mapper.UserMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Unit tests for {@link AuthServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String VALID_USERNAME = "TestUser12";
    private static final String VALID_PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access-token";

    @Mock
    private UserMapper userMapper;

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
            GIVEN username already exists
            WHEN register is called
            THEN throws UserAlreadyExistsException and does not save user
            """)
    void givenExistingUsername_whenRegister_thenThrowsUserAlreadyExistsException() {
        // GIVEN
        AuthRequest request = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);
        given(usersRepository.existsByUsername(VALID_USERNAME)).willReturn(true);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists: " + VALID_USERNAME);

        verify(userMapper, never()).toUser(any(AuthRequest.class));
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("""
            GIVEN username does not exist
            WHEN register is called
            THEN maps and saves user
            """)
    void givenNewUsername_whenRegister_thenMapsAndSavesUser() {
        // GIVEN
        AuthRequest request = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);
        User user = User.builder()
                .username(VALID_USERNAME)
                .passwordHash("encoded-password")
                .build();

        given(usersRepository.existsByUsername(VALID_USERNAME)).willReturn(false);
        given(userMapper.toUser(request)).willReturn(user);

        // WHEN
        authService.register(request);

        // THEN
        verify(userMapper).toUser(request);
        verify(usersRepository).save(user);
    }

    @Test
    @DisplayName("""
            GIVEN valid credentials
            WHEN login is called
            THEN returns auth response with access token
            """)
    void givenValidCredentials_whenLogin_thenReturnsAuthResponse() {
        // GIVEN
        AuthRequest request = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(VALID_USERNAME, VALID_PASSWORD);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtService.generateAccessToken(VALID_USERNAME)).willReturn(ACCESS_TOKEN);

        // WHEN
        AuthResponse actualResponse = authService.login(request);

        // THEN
        assertThat(actualResponse).isEqualTo(new AuthResponse(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("""
            GIVEN invalid credentials
            WHEN login is called
            THEN throws BadCredentialsException with generic message
            """)
    void givenInvalidCredentials_whenLogin_thenThrowsBadCredentialsException() {
        // GIVEN
        AuthRequest request = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Wrong password"));

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("""
            GIVEN authentication manager fails with any authentication exception
            WHEN login is called
            THEN translates it to BadCredentialsException
            """)
    void givenAuthenticationException_whenLogin_thenThrowsBadCredentialsException() {
        // GIVEN
        AuthRequest request = new AuthRequest(VALID_USERNAME, VALID_PASSWORD);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new AuthenticationException("Auth failed") {
                });

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }
}
