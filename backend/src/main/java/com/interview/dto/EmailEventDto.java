package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEventDto {
    private String email;
    // e.g., "CUSTOMER_CREATED"
    private String eventType;
    // Schema-free JSON data
    private Map<String, Object> additionalData;
}