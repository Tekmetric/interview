package com.interview.dto;

import com.interview.domain.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import static com.interview.service.VehicleService.PRODUCTION_YEAR_PATTERN_AS_STRING;
import static com.interview.service.VehicleService.VIN_PATTERN_AS_STRING;

@Builder
public record UpsertVehicleDto(
        @NotNull
        VehicleType type,
        @NotNull
        @Pattern(regexp = PRODUCTION_YEAR_PATTERN_AS_STRING)
        String productionYear,
        @NotEmpty
        @Pattern(regexp = VIN_PATTERN_AS_STRING)
        String vin,
        @NotEmpty
        String model,
        @NotEmpty
        String make
) {

}
