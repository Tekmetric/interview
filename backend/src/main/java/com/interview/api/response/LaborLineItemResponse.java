package com.interview.api.response;

import java.util.UUID;

public record LaborLineItemResponse(UUID id, String name, int quantity, UUID serviceCode) {}
