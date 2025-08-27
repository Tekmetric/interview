package com.interview.dto.search;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record PageRequestDto(
        @NotNull
        @Min(0)
        Integer pageNumber,

        @NotNull
        @Min(1)
        int pageSize,

        @Valid
        List<SortBy> sortBy) {
}
