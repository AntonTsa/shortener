package com.anton.tsarenko.shortener.url.util;

/**
 * This class is a fixture for testing {@link ShortUrlGenerator}.
 */
class ShortUrlGeneratorFixture {
    /** Minimal allowed short code length. */
    static final int MIN_SHORT_CODE_LENGTH = 6;

    /** Maximal allowed short code length. */
    static final int MAX_SHORT_CODE_LENGTH = 8;

    /** Number of generated samples for randomized checks. */
    static final int GENERATION_ITERATIONS = 100;

    /** Allowed characters in generated short codes. */
    static final String SHORT_CODE_REGEX = "^[A-Za-z0-9]+$";
}
