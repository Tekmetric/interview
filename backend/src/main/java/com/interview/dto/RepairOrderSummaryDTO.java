package com.interview.dto;

import java.time.OffsetDateTime;

public record RepairOrderSummaryDTO(
    Long id,
    String description,
    String status,
    OffsetDateTime createdDate,
    OffsetDateTime updatedDate
) {

}
