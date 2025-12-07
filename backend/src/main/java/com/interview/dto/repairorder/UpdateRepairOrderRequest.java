package com.interview.dto.repairorder;

import com.interview.model.RepairOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRepairOrderRequest(
        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        @NotBlank
        @Size(max = 255)
        String issueDescription,

        @NotNull
        @Schema(description = "Status of the repair order", example = "DRAFT")
        RepairOrderStatus status
) {
}
