package com.interview.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for subscription operations.
 */
public record SubscriptionRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId
) {}