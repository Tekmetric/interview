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

}
