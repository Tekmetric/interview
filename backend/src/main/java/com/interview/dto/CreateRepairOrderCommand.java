package com.interview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public record CreateRepairOrderCommand(
    @NotBlank String description,
    @NotBlank String vehicleMake,
    @NotBlank String vehicleModel,
    @NotNull @Positive Integer vehicleYear,
    String licensePlate,
    @NotNull UUID customerId,
    @Valid List<CreateLineItemCommand> lineItems
) {

  public CreateRepairOrderCommand {
    lineItems = lineItems == null ? null : List.copyOf(lineItems);
  }
}
