package com.interview.dto;

import com.interview.entity.WorkOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record WorkOrderUpdateRequest(
    @NotNull(message = "status is required")
    WorkOrderStatus status,

    @NotBlank(message = "summary is required")
    @Size(max = 150, message = "summary cannot exceed 150 characters")
    String summary,

    @Size(max = 1000, message = "notes cannot exceed 1000 characters")
    String notes,

    @NotNull(message = "laborRate is required")
    @DecimalMin(value = "0.01", message = "laborRate must be greater than 0")
    BigDecimal laborRate,

    @NotNull(message = "laborTime is required")
    @DecimalMin(value = "0.01", message = "laborTime must be greater than 0")
    BigDecimal laborTime,

    @Valid
    @NotEmpty(message = "partsNeeded must include at least one part")
    List<WorkOrderPartRequest> partsNeeded
) {
}
