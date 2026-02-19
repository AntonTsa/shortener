package com.anton.tsarenko.shortener.auth.service;

/**
 * This class serves as a fixture for testing the JwtService class.
 * It provides sample data to facilitate testing of the JwtService.
 */
public class JwtServiceFixture {
    /** A sample secret key for signing JWTs. */
    static final String SECRET_KEY = "test-test-test-test-test-test-test-test";
    /** A sample expiration time for JWTs in minutes. */
    static final long EXPIRATION_IN_MINUTES = 30L;
    /** A sample username to be used in JWT claims. */
    static final String USERNAME = "TestUser12";
}
