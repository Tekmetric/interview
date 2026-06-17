package com.interview.dto.workitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateWorkItemRequest(

        @Schema(description = "Name of the work item", example = "Oil Change")
        @NotBlank
        @Size(max = 255)
        String name,

        @Schema(description = "Detailed description of the work item", example = "Replace engine oil and filter")
        @NotBlank
        @Size(max = 255)
        String description,

        @Schema(description = "Price of the work item", example = "99.99")
        @Positive
        float price
) {
}
