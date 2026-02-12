package com.interview.dto.workitem;

import io.swagger.v3.oas.annotations.media.Schema;

public record WorkItemDto(

        @Schema(description = "Unique identifier of the work item", example = "1")
        Long id,

        @Schema(description = "Name of the work item", example = "Oil Change")
        String name,

        @Schema(description = "Detailed description of the work item", example = "Replace engine oil and filter")
        String description,

        @Schema(description = "Price of the work item", example = "99.99")
        float price
) {
}
