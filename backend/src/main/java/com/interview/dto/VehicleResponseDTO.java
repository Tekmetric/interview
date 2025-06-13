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

}
