package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(
    description = "Standard error response structure",
    example = """
    {
        "error": "ERROR_CODE",
        "message": "Error message describing what went wrong",
        "path": "/api/endpoint",
        "status": "{actual_status_code}",
        "timestamp": "2025-08-02T13:18:51.093649"
    }
    """
)
public record ErrorResponse(
    @Schema(description = "Error code identifying the type of error")
    String error,

    @Schema(description = "Human-readable error message")
    String message,

    @Schema(description = "Request path that caused the error")
    String path,

    @Schema(description = "HTTP status code")
    Integer status,

    @Schema(description = "Error timestamp")
    LocalDateTime timestamp
) {

    public static ErrorResponse of(String error, String message, String path, Integer status) {
        return new ErrorResponse(error, message, path, status, LocalDateTime.now());
    }
}