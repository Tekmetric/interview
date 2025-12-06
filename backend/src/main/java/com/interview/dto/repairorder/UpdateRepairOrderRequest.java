package com.interview.dto.repairorder;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateRepairOrderRequest(
        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        String issueDescription
) {
}
