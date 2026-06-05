package com.interview.dto;

import com.interview.entity.WorkOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderSummaryResponse(
    UUID id,
    UUID vehicleId,
    WorkOrderStatus status,
    String summary,
    String notes,
    BigDecimal laborRate,
    BigDecimal laborTime,
    BigDecimal laborCost,
    BigDecimal totalCost,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
