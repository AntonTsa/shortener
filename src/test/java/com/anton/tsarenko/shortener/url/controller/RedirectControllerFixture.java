package com.anton.tsarenko.shortener.url.controller;

/**
 * Fixture for RedirectController tests.
 */
class RedirectControllerFixture {
    /** Existing short code value. */
    static final String SHORT_CODE = "go1234";

    /** Original URL resolved by short code. */
    static final String ORIGINAL_URL = "https://example.com/landing";

    /** Redirect endpoint by short code. */
    static final String REDIRECT_ENDPOINT = "/api/V1/s_link/" + SHORT_CODE;
}
