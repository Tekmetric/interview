package com.interview.dto;

import java.time.LocalDateTime;

public record RepairOrderResponseDTO(
    Long id,
    String description,
    String status,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    VehicleDetailsDTO vehicle
) {

}
