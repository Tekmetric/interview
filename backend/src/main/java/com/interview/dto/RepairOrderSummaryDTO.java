package com.interview.dto;

import java.time.LocalDateTime;

public record RepairOrderSummaryDTO(
    Long id,
    String description,
    String status,
    LocalDateTime createdDate,
    LocalDateTime updatedDate
) {

}
