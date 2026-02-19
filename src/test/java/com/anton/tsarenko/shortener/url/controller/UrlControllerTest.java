package com.anton.tsarenko.shortener.url.controller;

import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.AUTHORIZATION_HEADER;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.BEARER_PREFIX;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.INVALID_BLANK_URL_REQUEST;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.LINKS_ENDPOINT;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.LINK_BY_ID_ENDPOINT;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.PAGE_RESPONSE;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.STORED_URL;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.URL_ID;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.URL_PAGE;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.URL_TO_CREATE;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.USER_ID;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.VALID_TOKEN;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.VALID_URL_REQUEST;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.VALID_USER;
import static com.anton.tsarenko.shortener.url.controller.UrlControllerFixture.VALID_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anton.tsarenko.shortener.auth.service.JwtService;
import com.anton.tsarenko.shortener.auth.service.UserService;
import com.anton.tsarenko.shortener.url.mapper.UrlMapper;
import com.anton.tsarenko.shortener.url.service.UrlService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for UrlController.
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = UrlController.class)
@ActiveProfiles("test")
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UrlService urlService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UserService userService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UrlMapper urlMapper;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private JwtService jwtService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeEach
    void setupAuthentication() {
        given(jwtService.isTokenValid(VALID_TOKEN)).willReturn(true);
        given(jwtService.extractUsername(VALID_TOKEN)).willReturn(VALID_USERNAME);
    }

    @Test
    @DisplayName("""
            GIVEN valid create request
            WHEN performing POST /api/v1/shortener/{userId}/links
            THEN returns 201 with location header
            """)
    void createValid() throws Exception {
        // GIVEN
        given(userService.getUserById(USER_ID)).willReturn(VALID_USER);
        given(urlMapper.toUrl(VALID_URL_REQUEST, VALID_USER)).willReturn(URL_TO_CREATE);
        given(urlService.createUrl(URL_TO_CREATE)).willReturn(URL_ID);

        // WHEN
        MockHttpServletResponse actualResponse =
                mockMvc.perform(post(LINKS_ENDPOINT)
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + VALID_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson()))
                        // THEN
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location", LINK_BY_ID_ENDPOINT))
                        .andReturn()
                        .getResponse();

        // AND THEN
        assertThat(actualResponse.getContentAsString()).isEmpty();
    }

    @Test
    @DisplayName("""
            GIVEN existing user urls
            WHEN performing GET /api/v1/shortener/{userId}/links
            THEN returns 200 with paged response
            """)
    void getAllUrlsValid() throws Exception {
        // GIVEN
        given(userService.getUserById(USER_ID)).willReturn(VALID_USER);
        given(urlService.retrieveAllUrlsByUser(any(), any())).willReturn(URL_PAGE);
        given(urlMapper.toPageResponse(URL_PAGE)).willReturn(PAGE_RESPONSE);

        // WHEN
        mockMvc.perform(get(LINKS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + VALID_TOKEN))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].id").value(URL_ID))
                .andExpect(jsonPath("$.content[0].originalUrl").value(STORED_URL.getOriginalUrl()))
                .andExpect(jsonPath("$.content[0].shortCode").value(STORED_URL.getShortCode()));
    }

    @Test
    @DisplayName("""
            GIVEN existing url id
            WHEN performing DELETE /api/v1/shortener/{userId}/links/{id}
            THEN returns 204 with empty body
            """)
    void deleteUrlValid() throws Exception {
        // GIVEN
        given(userService.getUserById(USER_ID)).willReturn(VALID_USER);
        doNothing().when(urlService).deleteUrl(URL_ID);

        // WHEN
        MockHttpServletResponse actualResponse =
                mockMvc.perform(delete(LINK_BY_ID_ENDPOINT)
                                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + VALID_TOKEN))
                        // THEN
                        .andExpect(status().isNoContent())
                        .andReturn()
                        .getResponse();

        // AND THEN
        assertThat(actualResponse.getContentAsString()).isEmpty();
    }

    @Test
    @DisplayName("""
            GIVEN create request with blank originalUrl
            WHEN performing POST /api/v1/shortener/{userId}/links
            THEN returns 400 with validation message
            """)
    void createWhenOriginalUrlBlank() throws Exception {
        // GIVEN

        // WHEN / THEN
        mockMvc.perform(post(LINKS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(INVALID_BLANK_URL_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("originalUrl")));
    }

    @SneakyThrows(JsonProcessingException.class)
    private String toJson() {
        return objectMapper.writeValueAsString(UrlControllerFixture.VALID_URL_REQUEST);
    }

    @SneakyThrows(JsonProcessingException.class)
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows(JsonProcessingException.class)
    @SuppressWarnings("unused")
    private <T> T fromJson(String string, Class<T> type) {
        return Objects.nonNull(string) ? objectMapper.readValue(string, type) : null;
    }
}
