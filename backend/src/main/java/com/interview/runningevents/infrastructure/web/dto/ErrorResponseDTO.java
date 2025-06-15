package com.interview.runningevents.infrastructure.web.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard DTO for API error responses.
 * Enhanced to provide a more consistent and detailed error structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response containing details about an API error")
public class ErrorResponseDTO {

    /**
     * HTTP status code.
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * Error type description.
     */
    @Schema(description = "Error type description", example = "Bad Request")
    private String error;

    /**
     * Error message.
     */
    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    /**
     * Timestamp of when the error occurred.
     */
    @Builder.Default
    @Schema(description = "Timestamp of when the error occurred (epoch milliseconds)", example = "1617181200000")
    private long timestamp = Instant.now().toEpochMilli();

    /**
     * Request path that caused the error.
     */
    @Schema(description = "Request path that caused the error", example = "/api/events/999")
    private String path;

    /**
     * Detailed validation errors, if applicable.
     */
    @Builder.Default
    @Schema(description = "Detailed validation errors, if applicable")
    private List<ValidationErrorDTO> details = new ArrayList<>();

    /**
     * Add a validation error to the details list.
     *
     * @param field   The field that has the validation error
     * @param message The validation error message
     * @return This ErrorResponseDTO instance for method chaining
     */
    public ErrorResponseDTO addValidationError(String field, String message) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(new ValidationErrorDTO(field, message));
        return this;
    }

    /**
     * Add a general error message to the details list.
     *
     * @param message The error message
     * @return This ErrorResponseDTO instance for method chaining
     */
    public ErrorResponseDTO addDetail(String message) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(new ValidationErrorDTO(null, message));
        return this;
    }

    /**
     * Nested class for field-specific validation errors.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field-specific validation error")
    public static class ValidationErrorDTO {
        /**
         * Field name with the validation error.
         * Can be null for general errors.
         */
        @Schema(description = "Field name with the validation error", example = "name")
        private String field;

        /**
         * Validation error message.
         */
        @Schema(description = "Validation error message", example = "Name is required")
        private String message;
    }
}
