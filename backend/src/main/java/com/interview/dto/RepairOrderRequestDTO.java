package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairOrderRequestDTO(
    @NotNull(message = "Vehicle ID is required")
    Long vehicleId,

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    String status
) {

  // Factory method for creating with required fields
  public static RepairOrderRequestDTO of(Long vehicleId, String description, String status) {
    return new RepairOrderRequestDTO(vehicleId, description, status);
  }

}
