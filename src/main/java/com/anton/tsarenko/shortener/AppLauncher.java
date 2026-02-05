package com.anton.tsarenko.shortener;

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
        SpringApplication.run(AppLauncher.class, args);
    }
}
