package com.anton.tsarenko.shortener.auth.controller;

import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.BAD_CREDENTIALS_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.BAD_REQUEST_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME_MESSAGE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.EXISTED_USERNAME_USER;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.INVALID_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.LOGIN_ENDPOINT;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.REGISTRATION_ENDPOINT;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_AUTH_REQUEST;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_AUTH_RESPONSE;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_TOKEN;
import static com.anton.tsarenko.shortener.auth.controller.AuthControllerFixture.VALID_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anton.tsarenko.shortener.auth.dto.AuthResponse;
import com.anton.tsarenko.shortener.auth.dto.ExceptionResponse;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.mapper.UserMapper;
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
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private AuthService authService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private JwtService jwtService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    @DisplayName("""
            GIVEN valid AuthRequest object
            WHEN performing POST /registration
            THEN returns 201 with empty body
            """)
    void registrationValid() throws Exception {
        // GIVEN
        given(userMapper.toUser(VALID_AUTH_REQUEST)).willReturn(VALID_USER);
        doNothing().when(authService).register(VALID_USER);

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
            GIVEN valid AuthRequest object, containing existing username
            WHEN performing POST /registration
            THEN returns 409 and corresponding error
            """)
    void registrationExistingUsername() throws Exception {
        // GIVEN
        given(userMapper.toUser(EXISTED_USERNAME_AUTH_REQUEST)).willReturn(EXISTED_USERNAME_USER);
        doThrow(new UserAlreadyExistsException(EXISTED_USERNAME))
                .when(authService).register(EXISTED_USERNAME_USER);

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
            THEN returns 400 and message "Bad Request" with details about validation errors
            """)
    void registrationInvalid() throws Exception {
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
            WHEN performing POST /registration
            THEN returns 500 and corresponding error
            """)
    void registrationRuntimeException() throws Exception {
        // GIVEN
        doThrow(new RuntimeException("boom")).when(authService).register(any(User.class));

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
            GIVEN valid AuthRequest object
            WHEN performing POST /login
            THEN returns 200 and accessToken in body
            """)
    void loginValid() throws Exception {
        // GIVEN
        given(userMapper.toUser(VALID_AUTH_REQUEST)).willReturn(VALID_USER);
        given(authService.login(VALID_USER)).willReturn(VALID_TOKEN);

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
            GIVEN valid AuthRequest object with bad credentials
            WHEN performing POST /login
            THEN returns 401 and "Bad Credentials" message
            """)
    void loginBadCredentials() throws Exception {
        // GIVEN
        given(userMapper.toUser(VALID_AUTH_REQUEST)).willReturn(VALID_USER);
        doThrow(new BadCredentialsException(BAD_CREDENTIALS_MESSAGE))
                .when(authService).login(VALID_USER);

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
            GIVEN invalid AuthRequest object
            WHEN performing POST /login
            THEN returns 400 and message "Bad Request" with details about validation errors
            """)
    void loginInvalid() throws Exception {
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
