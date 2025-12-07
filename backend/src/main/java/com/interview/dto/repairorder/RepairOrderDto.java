package com.interview.dto.repairorder;

import com.interview.model.RepairOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record RepairOrderDto(
        @Schema(description = "Unique car identifier", example = "2")
        Long id,

        @Schema(description = "Unique vehicle identification number", example = "WAUZZZ8V3JA123456")
        String vin,

        @Schema(description = "Car model", example = "Audi A3")
        String carModel,


        @Schema(description = "Short description of the car issue", example = "Car doesn't start")
        String issueDescription,

        @Schema(description = "Status of the repair order", example = "DRAFT")
        RepairOrderStatus status
) {
}
