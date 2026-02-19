package com.anton.tsarenko.shortener.url.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.anton.tsarenko.shortener.AppLauncher;
import com.anton.tsarenko.shortener.PostgresTestContainer;
import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.repo.UsersRepository;
import com.anton.tsarenko.shortener.url.entity.Url;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for UrlRepository with real PostgreSQL via Testcontainers.
 */
@SpringBootTest(classes = AppLauncher.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=true")
class UrlRepositoryIntegrationTest extends PostgresTestContainer {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("""
            GIVEN persisted url in Postgres container
            WHEN finding url by short code
            THEN repository returns stored entity
            """)
    void findByShortCodeReturnsStoredUrl() {
        // GIVEN
        User user = usersRepository.save(User.builder()
                .username("it_" + UUID.randomUUID().toString().substring(0, 8))
                .passwordHash("hash")
                .build());
        String shortCode = "abc123xy";
        Url url = Url.builder()
                .user(user)
                .originalUrl("https://example.com/" + UUID.randomUUID())
                .shortCode(shortCode)
                .expiredAt(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();
        urlRepository.save(url);

        // WHEN
        Optional<Url> found = urlRepository.findByShortCode(shortCode);

        // THEN
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getOriginalUrl()).isEqualTo(url.getOriginalUrl());
        assertThat(found.orElseThrow().getUser().getId()).isEqualTo(user.getId());
    }
}
