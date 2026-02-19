package com.anton.tsarenko.shortener.exceptions;

/**
 * Exception thrown when URL by short code is not found.
 */
public class UrlNotFoundException extends RuntimeException {
    /**
     * Creates UrlNotFoundException with message.
     *
     * @param message error message
     */
    public UrlNotFoundException(String message) {
        super(message);
    }
}
