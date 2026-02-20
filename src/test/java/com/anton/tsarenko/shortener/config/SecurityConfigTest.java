package com.anton.tsarenko.shortener.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for {@link SecurityConfig}.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private DefaultSecurityFilterChain securityFilterChain;

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("""
            GIVEN SecurityConfig
            WHEN passwordEncoder bean is created
            THEN it encodes and matches the raw password
            """)
    void securityConfig_PasswordEncoderCreated() {
        // GIVEN
        String rawPassword = "password123";

        // WHEN
        PasswordEncoder actualEncoder = securityConfig.passwordEncoder();
        String encodedPassword = actualEncoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword).isNotBlank();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(actualEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("""
            GIVEN SecurityConfig and data source
            WHEN userDetailsService bean is created
            THEN returns JdbcUserDetailsManager with expected queries
            """)
    void dataSource_UserDetailsServiceCreated() {
        // GIVEN

        // WHEN
        UserDetailsService actualService = securityConfig.userDetailsService(dataSource);

        // THEN
        assertThat(actualService).isInstanceOf(JdbcUserDetailsManager.class);

        String actualUsersByUsernameQuery = (String) ReflectionTestUtils.getField(
                actualService,
                "usersByUsernameQuery"
        );
        String actualAuthoritiesByUsernameQuery = (String) ReflectionTestUtils.getField(
                actualService,
                "authoritiesByUsernameQuery"
        );

        assertThat(actualUsersByUsernameQuery)
                .isEqualTo("select username, password_hash as password, enabled "
                        + "from users "
                        + "where username = ?");
        assertThat(actualAuthoritiesByUsernameQuery)
                .isEqualTo("select username, 'ROLE_USER' as authority "
                        + "from users "
                        + "where username = ?");
    }

    @Test
    @DisplayName("""
            GIVEN AuthenticationConfiguration
            WHEN authenticationManager bean is created
            THEN returns manager from AuthenticationConfiguration
            """)
    void authenticationConfiguration_AuthenticationManagerCreated() {
        // GIVEN
        given(authenticationConfiguration.getAuthenticationManager())
                .willReturn(authenticationManager);

        // WHEN
        AuthenticationManager actualManager =
                securityConfig.authenticationManager(authenticationConfiguration);

        // THEN
        assertThat(actualManager).isSameAs(authenticationManager);
    }

    @Test
    @DisplayName("""
            GIVEN HttpSecurity and JwtAuthFilter
            WHEN securityFilterChain bean is created
            THEN configures chain and returns built SecurityFilterChain
            """)
    void httpSecurity_SecurityFilterChainCreated() {
        // GIVEN
        given(httpSecurity.csrf(any())).willReturn(httpSecurity);
        given(httpSecurity.httpBasic(any())).willReturn(httpSecurity);
        given(httpSecurity.formLogin(any())).willReturn(httpSecurity);
        given(httpSecurity.sessionManagement(any())).willReturn(httpSecurity);
        given(httpSecurity.authorizeHttpRequests(any())).willReturn(httpSecurity);
        given(httpSecurity.addFilterBefore(
                eq(jwtAuthFilter),
                eq(UsernamePasswordAuthenticationFilter.class)
        )).willReturn(httpSecurity);
        given(httpSecurity.build()).willReturn(securityFilterChain);

        // WHEN
        SecurityFilterChain actualChain =
                securityConfig.securityFilterChain(httpSecurity, jwtAuthFilter);

        // THEN
        assertThat(actualChain).isSameAs(securityFilterChain);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).httpBasic(any());
        verify(httpSecurity).formLogin(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);
        verify(httpSecurity).build();
    }
}
