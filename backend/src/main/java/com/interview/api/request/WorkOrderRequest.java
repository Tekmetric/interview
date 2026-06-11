package com.interview.api.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record WorkOrderRequest(
        @NotNull Instant scheduledStartDateTime,
        @NotNull UUID customerId,
        @NotNull UUID vehicleId) {}
