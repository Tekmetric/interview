package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressFBWarnings(value = "EI_EXPOSE_REP",
    justification = "Records are immutable; list contents are not mutated")
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
