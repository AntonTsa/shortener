package com.anton.tsarenko.shortener.auth.mapper.impl;

import static com.anton.tsarenko.shortener.auth.mapper.impl.UserMapperImplFixture.ENCODED_PASSWORD;
import static com.anton.tsarenko.shortener.auth.mapper.impl.UserMapperImplFixture.RAW_PASSWORD;
import static com.anton.tsarenko.shortener.auth.mapper.impl.UserMapperImplFixture.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("""
            GIVEN valid auth request
            WHEN toUserForRegistration is called
            THEN maps username and encoded password to user entity
            """)
    void givenValidAuthRequest_whenToUserForRegistration_thenMapsToUserEntity() {
        // GIVEN
        UserMapperImpl mapper = new UserMapperImpl(passwordEncoder);
        AuthRequest request = new AuthRequest(USERNAME, RAW_PASSWORD);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        // WHEN
        User actualUser = mapper.toUserForRegistration(request);

        // THEN
        assertThat(actualUser.getUsername()).isEqualTo(USERNAME);
        assertThat(actualUser.getPasswordHash()).isEqualTo(ENCODED_PASSWORD);
        assertThat(actualUser.getId()).isNull();
        assertThat(actualUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("""
            GIVEN auth request with raw password
            WHEN toUserForRegistration is called
            THEN password encoder is invoked with request password
            """)
    void givenAuthRequest_whenToUserForRegistration_thenUsesPasswordEncoder() {
        // GIVEN
        UserMapperImpl mapper = new UserMapperImpl(passwordEncoder);
        AuthRequest request = new AuthRequest(USERNAME, RAW_PASSWORD);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        // WHEN
        mapper.toUserForRegistration(request);

        // THEN
        verify(passwordEncoder).encode(RAW_PASSWORD);
    }

    @Test
    @DisplayName("""
            GIVEN valid auth request
            WHEN toUserForLogin is called
            THEN maps raw password without encoding
            """)
    void givenValidAuthRequest_whenToUserForLogin_thenMapsRawPassword() {
        // GIVEN
        UserMapperImpl mapper = new UserMapperImpl(passwordEncoder);
        AuthRequest request = new AuthRequest(USERNAME, RAW_PASSWORD);

        // WHEN
        User actualUser = mapper.toUserForLogin(request);

        // THEN
        assertThat(actualUser.getUsername()).isEqualTo(USERNAME);
        assertThat(actualUser.getPasswordHash()).isEqualTo(RAW_PASSWORD);
        verify(passwordEncoder, never()).encode(RAW_PASSWORD);
    }
}
