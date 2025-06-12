package com.interview.dto;

public record CustomerSummaryDTO(
    Long id,
    String name,
    String email,
    String phoneNumber,
    int vehicleCount
) {

}
