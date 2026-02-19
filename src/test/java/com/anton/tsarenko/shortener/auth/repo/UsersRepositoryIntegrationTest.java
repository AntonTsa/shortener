package com.anton.tsarenko.shortener.auth.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.shortener.AppLauncher;
import com.anton.tsarenko.shortener.PostgresTestContainer;
import com.anton.tsarenko.shortener.auth.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for UsersRepository with real PostgreSQL via Testcontainers.
 */
@SpringBootTest(classes = AppLauncher.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=true")
class UsersRepositoryIntegrationTest extends PostgresTestContainer {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("""
            GIVEN persisted user in Postgres container
            WHEN checking existence by username
            THEN repository returns true
            """)
    void existsByUsernameReturnsTrueForPersistedUser() {
        // GIVEN
        String username = "it_" + UUID.randomUUID().toString().substring(0, 8);
        User user = User.builder()
                .username(username)
                .passwordHash("hash")
                .build();
        usersRepository.save(user);

        // WHEN
        boolean exists = usersRepository.existsByUsername(username);

        // THEN
        assertThat(exists).isTrue();
    }
}
