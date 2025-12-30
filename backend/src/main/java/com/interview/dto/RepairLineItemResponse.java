package com.interview.dto;

import java.math.BigDecimal;

public record RepairLineItemResponse(
        Long id,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
