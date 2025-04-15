package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic wrapper for standardized API responses.
 * @param <T> the type of data contained in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard response wrapper for all API endpoints")
public class ApiResponseDTO<T> {
    
    @Schema(description = "Indicates whether the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "A message describing the result of the operation", example = "Repair service created successfully")
    private String message;

    @Schema(description = "The data payload of the response")
    private T data;

    @Schema(description = "The timestamp when the response was generated", example = "2025-04-15T21:54:03")
    private LocalDateTime timestamp;
}
