package com.anton.tsarenko.shortener.url.service.impl;

import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.CREATED_URL_ID;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.ORIGINAL_URL;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.PAGEABLE;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.SAVED_URL;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.SHORT_CODE;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.URL_PAGE;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.URL_TO_CREATE;
import static com.anton.tsarenko.shortener.url.service.impl.UrlServiceImplFixture.VALID_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.anton.tsarenko.shortener.exceptions.custom.UrlNotFoundException;
import com.anton.tsarenko.shortener.url.entity.Url;
import com.anton.tsarenko.shortener.url.repo.UrlRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

/**
 * Unit tests for {@link UrlServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    @DisplayName("""
            GIVEN valid Url entity
            WHEN createUrl is called
            THEN sets short code, saves url and returns created id
            """)
    void createUrlValid() {
        // GIVEN
        given(urlRepository.existsByShortCode(anyString())).willReturn(false);
        given(urlRepository.save(URL_TO_CREATE)).willReturn(SAVED_URL);

        // WHEN
        Long actualId = urlService.createUrl(URL_TO_CREATE);

        // THEN
        assertThat(actualId).isEqualTo(CREATED_URL_ID);
        assertThat(URL_TO_CREATE.getShortCode()).isNotBlank();
        verify(urlRepository).save(URL_TO_CREATE);
    }

    @Test
    @DisplayName("""
            GIVEN existing user and pageable
            WHEN retrieveAllUrlsByUser is called
            THEN delegates to repository and returns page
            """)
    void retrieveAllUrlsByUserValid() {
        // GIVEN
        given(urlRepository.findAllByUser(VALID_USER, PAGEABLE)).willReturn(URL_PAGE);

        // WHEN
        Page<Url> actualPage = urlService.retrieveAllUrlsByUser(VALID_USER, PAGEABLE);

        // THEN
        assertThat(actualPage).isEqualTo(URL_PAGE);
    }

    @Test
    @DisplayName("""
            GIVEN existing url id
            WHEN deleteUrl is called
            THEN delegates deletion to repository
            """)
    void deleteUrlValid() {
        // GIVEN

        // WHEN
        urlService.deleteUrl(CREATED_URL_ID);

        // THEN
        verify(urlRepository).deleteById(CREATED_URL_ID);
    }

    @Test
    @DisplayName("""
            GIVEN existing short code
            WHEN resolveOriginalUrlAndIncreaseRedirectCount is called
            THEN increments redirects and returns original url
            """)
    void resolveOriginalUrlAndIncreaseRedirectCountValid() {
        // GIVEN
        given(urlRepository.findByShortCode(SHORT_CODE)).willReturn(Optional.of(SAVED_URL));
        given(urlRepository.save(SAVED_URL)).willReturn(SAVED_URL);

        // WHEN
        String actualOriginalUrl = urlService.resolveOriginalUrlAndIncreaseRedirectCount(
                SHORT_CODE
        );

        // THEN
        assertThat(actualOriginalUrl).isEqualTo(ORIGINAL_URL);
        assertThat(SAVED_URL.getRedirectsCount()).isEqualTo(4L);
        verify(urlRepository).save(SAVED_URL);
    }

    @Test
    @DisplayName("""
            GIVEN unknown short code
            WHEN resolveOriginalUrlAndIncreaseRedirectCount is called
            THEN throws UrlNotFoundException
            """)
    void resolveOriginalUrlAndIncreaseRedirectCountWhenShortCodeNotFound() {
        // GIVEN
        given(urlRepository.findByShortCode(SHORT_CODE)).willReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> urlService.resolveOriginalUrlAndIncreaseRedirectCount(SHORT_CODE))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining(SHORT_CODE);
    }
}
