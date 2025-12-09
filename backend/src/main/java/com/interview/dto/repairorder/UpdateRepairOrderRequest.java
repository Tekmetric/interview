package com.interview.dto.repairorder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRepairOrderRequest(
        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        @NotBlank
        @Size(max = 255)
        String issueDescription
) {
}
