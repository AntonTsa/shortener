package com.anton.tsarenko.shortener;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base test class that starts a shared PostgreSQL Testcontainer and registers datasource props.
 * Extend this class in test classes to reuse the container and the DynamicPropertySource.
 */
public abstract class PostgresTestContainer {

    /**
     * Using a static container to share it across all test classes that extend this base class.
     * This way we avoid starting and stopping the container for each test class,
     * which can be time-consuming.
     */
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.4")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("JWT_SECRET", () -> "test-test-test-test-test-test-test-test");
    }
}
