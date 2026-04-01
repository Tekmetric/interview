package com.interview.events;

import java.time.Instant;

public record RepairOrderCompletedEvent(
        Long repairOrderId,
        String customerName,
        Instant completedAt
) {}
