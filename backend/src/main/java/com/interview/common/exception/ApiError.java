package com.interview.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String error,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        String path,
        Map<String, String> validationErrors
) {
    public ApiError(int status, String error, String message, String path) {
        this(status, error, message, LocalDateTime.now(), path, null);
    }

    public ApiError(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(status, error, message, LocalDateTime.now(), path, validationErrors);
    }
}
