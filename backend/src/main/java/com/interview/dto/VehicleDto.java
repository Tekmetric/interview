package com.interview.dto;

import com.interview.domain.VehicleType;
import lombok.Builder;

import java.time.Instant;
import java.time.Year;

@Builder
public record VehicleDto(
        Long id,
        VehicleType type,
        Year productionYear,
        String vin,
        String model,
        String make,
        Instant createdDate,
        Instant lastModifiedDate,
        String createdBy,
        String lastModifiedBy
) {
}
