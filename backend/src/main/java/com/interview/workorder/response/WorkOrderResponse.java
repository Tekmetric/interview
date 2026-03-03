package com.interview.workorder.response;

import java.time.LocalDateTime;

public record WorkOrderResponse(
        Long id,
        String customerName,
        String vin,
        String issueDescription,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
