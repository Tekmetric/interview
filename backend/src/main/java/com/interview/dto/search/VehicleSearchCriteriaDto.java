package com.interview.dto.search;

import com.interview.domain.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Set;

import static com.interview.service.VehicleService.PRODUCTION_YEAR_PATTERN_AS_STRING;

@Builder
public record VehicleSearchCriteriaDto(
        Set<VehicleType> includingVehicleTypes,
        Set<VehicleType> excludingVehicleTypes,
        @Pattern(regexp = PRODUCTION_YEAR_PATTERN_AS_STRING)
        String productionYearFrom,
        @Pattern(regexp = PRODUCTION_YEAR_PATTERN_AS_STRING)
        String productionYearTo,
        Set<String> includingVins,
        Set<String> excludingVins) {

    @Schema(hidden = true)
    public boolean isEmpty() {
        return (includingVehicleTypes == null || includingVehicleTypes.isEmpty()) &&
                (excludingVehicleTypes == null || excludingVehicleTypes.isEmpty()) &&
                productionYearFrom == null &&
                productionYearTo == null &&
                (includingVins == null || includingVins.isEmpty()) &&
                (excludingVins == null || excludingVins.isEmpty());
    }

}
