package com.anton.tsarenko.shortener.url.controller;

import static com.anton.tsarenko.shortener.url.controller.RedirectControllerFixture.ORIGINAL_URL;
import static com.anton.tsarenko.shortener.url.controller.RedirectControllerFixture.REDIRECT_ENDPOINT;
import static com.anton.tsarenko.shortener.url.controller.RedirectControllerFixture.SHORT_CODE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anton.tsarenko.shortener.auth.service.JwtService;
import com.anton.tsarenko.shortener.exceptions.custom.UrlNotFoundException;
import com.anton.tsarenko.shortener.url.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for RedirectController.
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = RedirectController.class)
@ActiveProfiles("test")
class RedirectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UrlService urlService;

    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private JwtService jwtService;

    @Test
    @DisplayName("""
            GIVEN existing short code
            WHEN performing GET /api/V1/s_link/{shortCode}
            THEN returns 302 with Location header to original URL
            """)
    void redirectByShortCodeValid() throws Exception {
        // GIVEN
        given(urlService.resolveOriginalUrlAndIncreaseRedirectCount(SHORT_CODE))
                .willReturn(ORIGINAL_URL);

        // WHEN / THEN
        mockMvc.perform(get(REDIRECT_ENDPOINT))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", ORIGINAL_URL));

        verify(urlService).resolveOriginalUrlAndIncreaseRedirectCount(SHORT_CODE);
    }

    @Test
    @DisplayName("""
            GIVEN unknown short code
            WHEN performing GET /api/V1/s_link/{shortCode}
            THEN returns 404
            """)
    void redirectByShortCodeWhenShortCodeNotFound() throws Exception {
        // GIVEN
        given(urlService.resolveOriginalUrlAndIncreaseRedirectCount(SHORT_CODE))
                .willThrow(new UrlNotFoundException(
                        "Url is not found by short code: " + SHORT_CODE
                ));

        // WHEN / THEN
        mockMvc.perform(get(REDIRECT_ENDPOINT))
                .andExpect(status().isNotFound());

        verify(urlService).resolveOriginalUrlAndIncreaseRedirectCount(SHORT_CODE);
    }
}
