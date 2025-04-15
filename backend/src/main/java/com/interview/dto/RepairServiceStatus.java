package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Possible status values for a repair service")
public enum RepairServiceStatus {
    @Schema(description = "Initial status when service request is created") PENDING,
    @Schema(description = "Vehicle has been diagnosed but awaiting customer approval") DIAGNOSED,
    @Schema(description = "Customer has approved the repair work") APPROVED,
    @Schema(description = "Repair work is currently being performed") IN_PROGRESS,
    @Schema(description = "Repair work has been completed") COMPLETED,
    @Schema(description = "Vehicle has been delivered back to the customer") DELIVERED,
    @Schema(description = "Service has been cancelled") CANCELLED
}
