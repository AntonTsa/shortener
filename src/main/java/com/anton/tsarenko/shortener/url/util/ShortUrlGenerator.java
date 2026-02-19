package com.anton.tsarenko.shortener.url.util;

import java.util.Random;

/**
 * Utility class for generating short URLs.
 */
public class ShortUrlGenerator {
    private static final Random RANDOM = new Random();

    /**
     * Generates a random short code for the URL.
     * The short code is a combination of letters and digits, and is 6-8 characters long.
     *
     * @return a randomly generated short code for the URL
     */
    public static String generateShortCode() {
        int length = RANDOM.nextInt(6, 9);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(characters.length());
            shortCode.append(characters.charAt(index));
        }
        return shortCode.toString();
    }
}
