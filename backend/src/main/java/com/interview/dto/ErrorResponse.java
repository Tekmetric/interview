package com.interview.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors,
        LocalDateTime timestamp
) {
    public ErrorResponse {
        timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
    }
}
