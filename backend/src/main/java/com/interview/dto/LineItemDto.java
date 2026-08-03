package com.interview.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LineItemDto(
    UUID id,
    String description,
    BigDecimal unitPrice,
    Integer version,
    Instant createdAt,
    Instant updatedAt
) {}
