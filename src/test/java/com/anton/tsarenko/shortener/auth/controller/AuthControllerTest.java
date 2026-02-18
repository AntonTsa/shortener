package com.anton.tsarenko.shortener.auth.controller;

import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.BAD_CREDENTIALS_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.BAD_REQUEST_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.INVALID_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.LOGIN_ENDPOINT;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.REGISTRATION_ENDPOINT;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_AUTH_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anton.tsarenko.shortener.PostgresTestContainer;
import com.anton.tsarenko.shortener.auth.dto.AuthRequest;
import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.dto.ExceptionResponse;
import com.anton.tsarenko.shortener.auth.service.AuthService;
import com.anton.tsarenko.shortener.auth.service.JwtService;
import com.anton.tsarenko.shortener.exceptions.UserAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for AuthController.
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest extends PostgresTestContainer {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private AuthService authService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private JwtService jwtService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    @DisplayName("""
            GIVEN valid registration payload
            WHEN POST /registration
            THEN returns 201 with empty body
            """)
    void givenValidRegistration_whenPostRegistration_thenCreated() throws Exception {
        // GIVEN
        doNothing().when(authService).register(VALID_AUTH_REQUEST);

        // WHEN
        MockHttpServletResponse actualResponse =
                mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(VALID_AUTH_REQUEST)))
                // THEN
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        // AND THEN
        assertThat(actualResponse.getContentAsString()).isEmpty();
    }

    @Test
    @DisplayName("""
            GIVEN existing username
            WHEN POST /registration
            THEN returns 409 and corresponding error
            """)
    void givenExistingUsername_whenPostRegistration_thenConflict() throws Exception {
        // GIVEN
        doThrow(new UserAlreadyExistsException(EXISTED_USERNAME))
                .when(authService).register(EXISTED_USERNAME_AUTH_REQUEST);

        // WHEN
        ExceptionResponse actualResponse = fromJson(
                mockMvc.perform(post(REGISTRATION_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(EXISTED_USERNAME_AUTH_REQUEST)))
                        // THEN
                        .andExpect(status().isConflict())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ExceptionResponse.class);

        // AND THEN
        assertThat(actualResponse.message()).isEqualTo(EXISTED_USERNAME_MESSAGE);
    }

    @Test
    @DisplayName("""
            GIVEN invalid registration payload
            WHEN POST /registration
            THEN returns 400 and corresponding error
            """)
    void givenInvalidRegistration_whenPostRegistration_thenBadRequest() throws Exception {
        // GIVEN

        // WHEN
        ExceptionResponse actualResponse = fromJson(
                mockMvc.perform(post(REGISTRATION_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(INVALID_AUTH_REQUEST)))
                        // THEN
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ExceptionResponse.class);

        // AND THEN
        assertThat(actualResponse.message()).isEqualTo(BAD_REQUEST_MESSAGE);
    }

    @Test
    @DisplayName("""
            GIVEN auth service throws runtime exception
            WHEN POST /registration
            THEN returns 500 and corresponding error
            """)
    void registration_runtimeException_returnsInternalServerError() throws Exception {
        // GIVEN
        doThrow(new RuntimeException("boom")).when(authService).register(any(AuthRequest.class));

        // WHEN
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(VALID_AUTH_REQUEST)))
                // THEN
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("boom"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("""
            GIVEN valid login payload
            WHEN POST /login
            THEN returns 200 and accessToken in body
            """)
    void givenValidLogin_whenPostLogin_thenOkAndToken() throws Exception {
        // GIVEN
        given(authService.login(VALID_AUTH_REQUEST)).willReturn(VALID_AUTH_RESPONSE);

        // WHEN
        AuthResponse actualResponse = fromJson(
                mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(VALID_AUTH_REQUEST)))
                        // THEN
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                AuthResponse.class);

        assertEquals(VALID_AUTH_RESPONSE, actualResponse);
    }

    @Test
    @DisplayName("""
            GIVEN bad credentials
            WHEN POST /login
            THEN returns 401 and service called
            """)
    void givenBadCredentials_whenPostLogin_thenUnauthorized() throws Exception {
        // GIVEN
        doThrow(new BadCredentialsException(BAD_CREDENTIALS_MESSAGE))
                .when(authService).login(VALID_AUTH_REQUEST);

        // WHEN
        ExceptionResponse actualResponse = fromJson(mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(VALID_AUTH_REQUEST)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString(),
                ExceptionResponse.class);

        assertThat(actualResponse.message()).isEqualTo(BAD_CREDENTIALS_MESSAGE);
    }

    @Test
    @DisplayName("""
            GIVEN invalid login payload
            WHEN POST /login
            THEN returns 400 and service not called
            """)
    void givenInvalidLogin_whenPostLogin_thenBadRequest() throws Exception {
        // GIVEN

        // WHEN
        ExceptionResponse actualResponse = fromJson(
                mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(INVALID_AUTH_REQUEST)))
                        // THEN
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ExceptionResponse.class);

        // AND THEN
        assertThat(actualResponse.message()).isEqualTo(BAD_REQUEST_MESSAGE);
    }

    @SneakyThrows(JsonProcessingException.class)
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows(JsonProcessingException.class)
    public <T> T fromJson(String string, Class<T> type) {
        return Objects.nonNull(string) ? objectMapper.readValue(string, type) : null;
    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("unused")
    public <T> T fromJson(String string, TypeReference<T> type) {
        return Objects.nonNull(string) ? objectMapper.readValue(string, type) : null;
    }
}
