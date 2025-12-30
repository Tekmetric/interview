package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RepairLineItemRequest(
        @NotBlank @Size(max=256) String description,
        @NotNull Integer quantity,
        @NotNull BigDecimal unitPrice
) {}
