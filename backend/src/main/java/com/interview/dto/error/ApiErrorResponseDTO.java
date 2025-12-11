package com.interview.dto.error;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * API error response DTO.
 * Standard structure for all error responses from the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String timestamp;
    private Integer status;
    private String message;
    private JsonObject errors;
}

