package com.anton.tsarenko.shortener.url.util;

import static com.anton.tsarenko.shortener.url.util.ShortUrlGeneratorFixture.GENERATION_ITERATIONS;
import static com.anton.tsarenko.shortener.url.util.ShortUrlGeneratorFixture.MAX_SHORT_CODE_LENGTH;
import static com.anton.tsarenko.shortener.url.util.ShortUrlGeneratorFixture.MIN_SHORT_CODE_LENGTH;
import static com.anton.tsarenko.shortener.url.util.ShortUrlGeneratorFixture.SHORT_CODE_REGEX;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ShortUrlGenerator}.
 */
class ShortUrlGeneratorTest {

    @Test
    @DisplayName("""
            GIVEN short code generator
            WHEN generateShortCode is called
            THEN returns non-empty code with length between 6 and 8
            """)
    void generateShortCodeLengthConstraints() {
        // GIVEN

        // WHEN
        String shortCode = ShortUrlGenerator.generateShortCode();

        // THEN
        assertThat(shortCode).isNotBlank();
        assertThat(shortCode.length())
                .isGreaterThanOrEqualTo(MIN_SHORT_CODE_LENGTH)
                .isLessThanOrEqualTo(MAX_SHORT_CODE_LENGTH);
    }

    @Test
    @DisplayName("""
            GIVEN short code generator
            WHEN generateShortCode is called multiple times
            THEN each code contains only alphanumeric characters
            """)
    void generateShortCodeAllowedCharacters() {
        // GIVEN

        // WHEN / THEN
        for (int i = 0; i < GENERATION_ITERATIONS; i++) {
            String shortCode = ShortUrlGenerator.generateShortCode();
            assertThat(shortCode).matches(SHORT_CODE_REGEX);
        }
    }
}
