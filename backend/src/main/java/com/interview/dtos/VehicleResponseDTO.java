package com.interview.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Builder
@Schema(description = "Vehicle")
public record VehicleResponseDTO(
        Long id,
        String vin,
        String make,
        String model,
        Integer manufactureYear,
        String licensePlate,
        String ownerName,
        String createdBy,
        Instant createdDate,
        String lastModifiedBy,
        Instant lastModifiedDate) implements VehicleDTOBase, BaseEntityDTO {
}
