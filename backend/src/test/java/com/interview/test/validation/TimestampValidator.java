package com.interview.test.validation;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimestampValidator {

    private static final int RECENT_TOLERATION = 1000;

    public static void validateRecentTimestamp(long timestamp) {
        assertTrue(timestamp > 0);

        Instant recentTimestamp = Instant.ofEpochMilli(timestamp);
        Instant now = Instant.now();

        assertTrue(now.isAfter(recentTimestamp));

        Duration duration = Duration.between(recentTimestamp, now);
        assertTrue(duration.toMillis() < RECENT_TOLERATION);
    }
}
