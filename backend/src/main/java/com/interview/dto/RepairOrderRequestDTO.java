package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairOrderRequestDTO(
    @NotNull(message = "Vehicle ID is required")
    Long vehicleId,

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotNull(message = "Status is required")
    RepairOrderStatus status
) {

  // Factory method for creating with required fields
  public static RepairOrderRequestDTO of(Long vehicleId, String description, RepairOrderStatus status) {
    return new RepairOrderRequestDTO(vehicleId, description, status);
  }

}
