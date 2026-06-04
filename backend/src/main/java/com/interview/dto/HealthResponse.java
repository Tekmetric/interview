package com.interview.dto;

import java.time.LocalDateTime;

public record HealthResponse(
    String status,
    LocalDateTime checkedAt
) {
}
