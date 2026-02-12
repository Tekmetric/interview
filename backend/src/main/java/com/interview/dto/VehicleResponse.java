package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleResponse(
    Long id,
    Long customerId,
    String customerName, // firstName + lastName
    String customerEmail,
    String vin,
    String make,
    String model,
    Integer year,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    String createdBy,
    String updatedBy
) {}