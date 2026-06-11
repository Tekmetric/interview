package com.interview.api.request;

import com.interview.domain.Vin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VehicleRequest(
        @Valid @NotNull Vin vin, @NotNull UUID customerId) {}
