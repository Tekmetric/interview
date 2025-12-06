package com.interview.dto.repairorder;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRepairOrderRequest(
        @Schema(description = "Unique vehicle identification number", example = "WAUZZZ8V3JA123456")
        String vin,

        @Schema(description = "Car model", example = "Audi A3")
        String carModel,

        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        String issueDescription
) {
}
