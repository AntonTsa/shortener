package com.anton.tsarenko.shortener.auth.mapper.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link UserMapperImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {
    private static final String USERNAME = "TestUser12";
    private static final String RAW_PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("""
            GIVEN valid auth request
            WHEN toUser is called
            THEN maps username and encoded password to user entity
            """)
    void givenValidAuthRequest_whenToUser_thenMapsToUserEntity() {
        // GIVEN
        UserMapperImpl mapper = new UserMapperImpl(passwordEncoder);
        AuthRequest request = new AuthRequest(USERNAME, RAW_PASSWORD);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        // WHEN
        User actualUser = mapper.toUser(request);

        // THEN
        assertThat(actualUser.getUsername()).isEqualTo(USERNAME);
        assertThat(actualUser.getPasswordHash()).isEqualTo(ENCODED_PASSWORD);
        assertThat(actualUser.getId()).isNull();
        assertThat(actualUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("""
            GIVEN auth request with raw password
            WHEN toUser is called
            THEN password encoder is invoked with request password
            """)
    void givenAuthRequest_whenToUser_thenUsesPasswordEncoder() {
        // GIVEN
        UserMapperImpl mapper = new UserMapperImpl(passwordEncoder);
        AuthRequest request = new AuthRequest(USERNAME, RAW_PASSWORD);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        // WHEN
        mapper.toUser(request);

        // THEN
        verify(passwordEncoder).encode(RAW_PASSWORD);
    }
}
