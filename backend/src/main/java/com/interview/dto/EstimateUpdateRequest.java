package com.interview.dto;

import com.interview.entity.EstimateStatus;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record EstimateUpdateRequest(
    @NotNull(message = "status is required")
    EstimateStatus status,

    Set<@NotNull(message = "workOrderIds cannot contain null values") UUID> workOrderIds
) {
}
