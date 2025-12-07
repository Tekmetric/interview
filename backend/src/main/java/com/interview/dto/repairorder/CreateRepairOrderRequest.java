package com.interview.dto.repairorder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRepairOrderRequest(
        @Schema(description = "Unique vehicle identification number", example = "WAUZZZ8V3JA123456")
        @NotBlank
        @Size(max = 255)
        String vin,

        @Schema(description = "Car model", example = "Audi A3")
        @NotBlank
        @Size(max = 255)
        String carModel,

        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        @NotBlank
        @Size(max = 255)
        String issueDescription
) {
}
