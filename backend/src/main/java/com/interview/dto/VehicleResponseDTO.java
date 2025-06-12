package com.interview.dto;

import java.util.List;

public record VehicleResponseDTO(
    Long id,
    String make,
    String model,
    Integer year,
    String licensePlate,
    CustomerSummaryDTO customer,
    List<RepairOrderSummaryDTO> repairOrders
) {

  // Factory method without repair orders (for basic responses)
  public static VehicleResponseDTO withoutRepairOrders(Long id, String make, String model,
      Integer year,
      String licensePlate, CustomerSummaryDTO customer) {
    return new VehicleResponseDTO(id, make, model, year, licensePlate, customer, null);
  }
}
