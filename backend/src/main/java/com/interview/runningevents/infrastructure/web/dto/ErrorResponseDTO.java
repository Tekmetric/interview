package com.interview.runningevents.infrastructure.web.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard DTO for API error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Error message.
     */
    private String message;

    /**
     * Timestamp of when the error occurred.
     */
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();

    /**
     * Request path that caused the error.
     */
    private String path;

    /**
     * Detailed validation errors, if applicable.
     */
    @Builder.Default
    private List<ValidationErrorDTO> errors = new ArrayList<>();

    /**
     * Add a validation error to the errors list.
     *
     * @param field The field that has the validation error
     * @param message The validation error message
     * @return This ErrorResponseDTO instance for method chaining
     */
    public ErrorResponseDTO addValidationError(String field, String message) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationErrorDTO(field, message));
        return this;
    }

    /**
     * Nested class for field-specific validation errors.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDTO {
        /**
         * Field name with the validation error.
         */
        private String field;

        /**
         * Validation error message.
         */
        private String message;
    }
}
