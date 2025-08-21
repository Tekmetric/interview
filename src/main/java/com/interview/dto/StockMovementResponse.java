package com.interview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMovementResponse(
    Long id,
    Long productId,
    String productSku,
    String productName,
    Long warehouseId,
    String warehouseName,
    String movementType,
    String movementReason,
    Integer quantity,
    BigDecimal unitCost,
    String referenceNumber,
    String notes,
    LocalDateTime createdAt
) {}