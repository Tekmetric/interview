package com.interview.dto.filter;

import lombok.Builder;

/**
 * Filter object for vehicle search operations.
 *
 * <p>Encapsulates all possible filter criteria for vehicle searches.
 * Used with JPA Specifications to build dynamic queries in a type-safe manner.
 */
@Builder
public record VehicleFilter(
    Long customerId,
    String vin,
    String make,
    String model,
    Integer minYear,
    Integer maxYear,
    String customerEmail,
    String customerName
) {
}