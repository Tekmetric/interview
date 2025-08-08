package com.interview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    String category,
    String unit,
    BigDecimal price,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}