package com.interview.model.dto;

import java.time.Instant;

public record JobCreateRequest(Integer carId,
                               String vin,
                               String make,
                               String model,
                               Integer modelYear,
                               String customer,
                               Instant scheduledAt) {
};