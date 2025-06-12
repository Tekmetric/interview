package com.interview.dto;

import java.time.OffsetDateTime;

public record RepairOrderResponseDTO(
    Long id,
    String description,
    String status,
    OffsetDateTime createdDate,
    OffsetDateTime updatedDate,
    VehicleDetailsDTO vehicle
) {

}
