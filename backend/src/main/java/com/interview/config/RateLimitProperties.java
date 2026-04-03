package com.interview.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for rate limiting.
 */
@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private int capacity = 1000;
    private int refillAmount = 1000;
    private Duration refillDuration = Duration.ofMinutes(1);
}
