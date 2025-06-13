package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import java.time.OffsetDateTime;

public record RepairOrderResponseDTO(
    Long id,
    String description,
    RepairOrderStatus status,
    OffsetDateTime createdDate,
    OffsetDateTime updatedDate,
    VehicleDetailsDTO vehicle
) {

}
