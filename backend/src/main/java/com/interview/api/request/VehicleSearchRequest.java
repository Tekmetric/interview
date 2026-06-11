package com.interview.api.request;

import jakarta.annotation.Nullable;
import java.util.UUID;

public record VehicleSearchRequest(@Nullable UUID customerId) {}
