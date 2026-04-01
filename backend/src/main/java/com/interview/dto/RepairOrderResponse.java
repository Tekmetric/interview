package com.interview.dto;

import com.interview.entity.RepairOrderStatus;
import java.math.BigDecimal;
import java.util.List;

/**
 * This record represents the response DTO for a RepairOrder.
 * It intentionally does not have every field from the RepairOrder entity to show we can expose only certain fields if desired.
 * This is good practice for performance because we only need to fetch and serialize the fields we actually want to expose via the API.
 * It also helps support a versioned API strategy.
 */
public record RepairOrderResponse(
        Long id,
        Long version,
        String orderNumber,
        String vin,
        Integer vehicleYear,
        String vehicleMake,
        String vehicleModel,
        String customerName,
        String customerPhone,
        RepairOrderStatus status,
        BigDecimal total,
        List<RepairLineItemResponse> lineItems
) {}
