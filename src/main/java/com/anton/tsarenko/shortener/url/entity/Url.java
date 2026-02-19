package com.anton.tsarenko.shortener.url.entity;

import com.anton.tsarenko.shortener.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a url.
 */
@Entity
@Getter
@Setter
@Builder
@Table(name = "urls")
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "redirects_count")
    @Builder.Default
    private Long redirectsCount = 0L;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    @Column(name = "expired_at")
    @Builder.Default
    private Instant expiredAt = Instant.now().plus(30, ChronoUnit.DAYS);

    @Column(name = "created_at", insertable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
