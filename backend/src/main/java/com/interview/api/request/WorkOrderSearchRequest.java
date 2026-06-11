package com.interview.api.request;

import jakarta.annotation.Nullable;
import java.util.UUID;

public record WorkOrderSearchRequest(
        @Nullable UUID customerId, @Nullable UUID vehicleId) {}
