package com.interview.dto.search;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VehicleSearchDto(
        @NotNull
        @Valid
        VehicleSearchCriteriaDto searchCriteriaDto,
        @NotNull
        @Valid
        PageRequestDto pageRequestDto) {
}
