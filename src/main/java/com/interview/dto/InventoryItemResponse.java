package com.interview.dto;

import java.time.LocalDateTime;

public record InventoryItemResponse(
    Long id,
    Long productId,
    String productSku,
    String productName,
    String productCategory,
    String productUnit,
    Long warehouseId,
    String warehouseName,
    String warehouseLocation,
    Integer quantityAvailable,
    Integer quantityReserved,
    Integer totalQuantity,
    Integer reorderPoint,
    Boolean belowReorderPoint,
    LocalDateTime lastMovementAt,
    LocalDateTime updatedAt
) {
    public InventoryItemResponse {
        totalQuantity = quantityAvailable + quantityReserved;
        belowReorderPoint = reorderPoint != null && quantityAvailable <= reorderPoint;
    }
}