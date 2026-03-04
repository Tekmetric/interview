package com.interview.workorder.dto;

import com.interview.workorder.model.WorkOrderStatus;
import java.time.LocalDateTime;

public record WorkOrderResponse(
        Long id,
        Long customerId,
        String vin,
        String issueDescription,
        WorkOrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
