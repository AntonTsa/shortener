package com.anton.tsarenko.shortener;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application launcher class.
 */
@SpringBootApplication
public class AppLauncher {
    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        loadEnvFromDotenvIfPresent();
        SpringApplication.run(AppLauncher.class, args);
    }

    /**
     * Loads key-value pairs from local .env file into JVM system properties.
     * Existing OS environment variables and JVM properties have higher priority.
     */
    private static void loadEnvFromDotenvIfPresent() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, entry.getValue());
            }
        });
    }
}
