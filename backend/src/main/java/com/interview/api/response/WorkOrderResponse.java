package com.interview.api.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkOrderResponse(
        UUID id,
        Instant scheduledStartDateTime,
        UUID customerId,
        UUID vehicleId,
        List<PartLineItemResponse> partLineItems,
        List<LaborLineItemResponse> laborLineItems) {}
