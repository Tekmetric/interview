package com.interview.dto.repairorder;

import com.interview.model.RepairOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateRepairOrderRequest(
        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        String issueDescription,

        @Schema(description = "Status of the repair order", example = "DRAFT")
        RepairOrderStatus status
) {
}
