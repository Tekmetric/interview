package com.interview.domain;

import java.time.Instant;
import java.util.UUID;

public record WorkOrder(UUID id, Instant scheduledStartDateTime, UUID customerId, UUID vehicleId) {}
