package com.interview.dto;

import java.util.List;

public record CustomerResponseDTO(
    Long id,
    String name,
    String email,
    String phoneNumber,
    String address,
    List<VehicleSummaryDTO> vehicles
) {

  // Factory method without vehicles (for basic responses)
  public static CustomerResponseDTO withoutVehicles(Long id, String name, String email,
      String phoneNumber, String address) {
    return new CustomerResponseDTO(id, name, email, phoneNumber, address, null);
  }

}
