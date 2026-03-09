package com.interview.api.request;

import com.interview.domain.Vin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(@Valid @NotNull Vin vin) {}
