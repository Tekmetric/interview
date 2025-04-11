package com.interview.dto;

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
public class ApiResponseDTO<T> {
    
    /**
     * Indicates whether the request was successful.
     */
    private boolean success;
    
    /**
     * A message describing the result of the operation.
     */
    private String message;
    
    /**
     * The data payload of the response.
     */
    private T data;
    
    /**
     * The timestamp when the response was generated.
     */
    private LocalDateTime timestamp;
}
