package com.interview.dtos;

import lombok.Builder;

import java.time.Instant;

@Builder
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
