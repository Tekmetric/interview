package com.interview.transactions.service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record Transaction  (
        Long id,
        BigDecimal amount,
        Currency currency,
        Status status,
        Instant createdAt,
        Instant updatedAt
) {}



