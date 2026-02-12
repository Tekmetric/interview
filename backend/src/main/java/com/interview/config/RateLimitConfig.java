package com.interview.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for rate limiting using Bucket4j.
 */
@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    private final RateLimitProperties rateLimitProperties;

    @Bean
    public Bucket rateLimitBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getCapacity())
                .refillIntervally(rateLimitProperties.getRefillAmount(), rateLimitProperties.getRefillDuration())
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
