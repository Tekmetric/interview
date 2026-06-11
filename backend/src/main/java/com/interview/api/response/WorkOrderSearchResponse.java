package com.interview.api.response;

import java.time.Instant;
import java.util.UUID;

public record WorkOrderSearchResponse(UUID id, Instant scheduledStartDateTime, UUID customerId, UUID vehicleId) {}
