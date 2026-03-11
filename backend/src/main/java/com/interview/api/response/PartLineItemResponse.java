package com.interview.api.response;

import java.util.UUID;

public record PartLineItemResponse(UUID id, String name, int quantity, UUID partNumber) {}
