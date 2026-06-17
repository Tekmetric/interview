package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
    description = "Error response with validation details",
    example = """
    {
        "error": "VALIDATION_ERROR",
        "message": "Request validation failed",
        "path": "/v1/endpoint",
        "status": 400,
        "timestamp": "2025-08-02T13:18:39.831663",
        "validationErrors": [
            {
                "field": "field_name",
                "rejectedValue": "",
                "message": "field_name is required"
            }
        ]
    }
    """
)
public record ValidationErrorResponse(
    @Schema(description = "Error code")
    String error,

    @Schema(description = "Human-readable error message")
    String message,

    @Schema(description = "Request path that caused the error")
    String path,

    @Schema(description = "HTTP status code")
    Integer status,

    @Schema(description = "Error timestamp")
    LocalDateTime timestamp,

    @Schema(description = "List of field validation errors")
    List<ValidationError> validationErrors
) {

    public static ValidationErrorResponse withValidationErrors(String error, String message, String path,
        Integer status, List<ValidationError> validationErrors) {
        return new ValidationErrorResponse(error, message, path, status, LocalDateTime.now(), validationErrors);
    }

    @Schema(description = "Individual field validation error")
    public record ValidationError(
        @Schema(description = "Field name that failed validation")
        String field,

        @Schema(description = "Value that was rejected")
        Object rejectedValue,

        @Schema(description = "Validation error message")
        String message
    ) {}
}