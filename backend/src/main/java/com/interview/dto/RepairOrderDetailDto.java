package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RepairOrderDetailDto(
    UUID id,
    String description,
    RepairOrderStatus status,
    String vehicleMake,
    String vehicleModel,
    Integer vehicleYear,
    String licensePlate,
    UUID customerId,
    List<LineItemDto> lineItems,
    Integer version,
    Instant createdAt,
    Instant updatedAt
) {}
