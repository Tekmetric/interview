package com.interview.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for RateLimitConfig to ensure proper bucket creation.
 */
@ExtendWith(MockitoExtension.class)
class RateLimitConfigTest {

    @Mock
    private RateLimitProperties rateLimitProperties;

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        rateLimitConfig = new RateLimitConfig(rateLimitProperties);
    }

    @Test
    void shouldCreateBucketWithConfiguredProperties() {
        // Given
        int capacity = 1000;
        int refillAmount = 1000;
        Duration refillDuration = Duration.ofMinutes(1);

        when(rateLimitProperties.getCapacity()).thenReturn(capacity);
        when(rateLimitProperties.getRefillAmount()).thenReturn(refillAmount);
        when(rateLimitProperties.getRefillDuration()).thenReturn(refillDuration);

        // When
        Bucket bucket = rateLimitConfig.rateLimitBucket();

        // Then
        assertThat(bucket).isNotNull();

        // Test that bucket allows consumption based on configured capacity
        boolean canConsumeAll = bucket.tryConsume(capacity);
        assertThat(canConsumeAll).isTrue();

        // Test that bucket is exhausted after consuming all tokens
        boolean cannotConsumeMore = bucket.tryConsume(1);
        assertThat(cannotConsumeMore).isFalse();
    }
}
