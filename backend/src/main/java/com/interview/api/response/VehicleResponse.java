package com.interview.api.response;

import com.interview.domain.Vin;
import java.util.UUID;

public record VehicleResponse(UUID id, Vin vin) {}
