package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateLineItemCommand(
    @NotBlank String description,
    @NotNull @Positive BigDecimal unitPrice
) {}
