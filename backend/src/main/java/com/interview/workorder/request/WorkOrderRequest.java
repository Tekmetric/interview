package com.interview.workorder.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WorkOrderRequest(
        @NotBlank(message = "vin is required")
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{11,17}$", message = "vin must be 11-17 uppercase alphanumeric characters")
        String vin,

        @NotBlank(message = "issueDescription is required")
        @Size(max = 500, message = "issueDescription cannot exceed 500 characters")
        String issueDescription,

        @NotBlank(message = "status is required")
        @Pattern(regexp = "OPEN|IN_PROGRESS|COMPLETED|CANCELLED", message = "status must be OPEN, IN_PROGRESS, COMPLETED, or CANCELLED")
        String status
) {
}
