package com.interview.dto.workitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateWorkItemRequest(

        @Schema(description = "Updated description of the work item", example = "Replaced oil and filter")
        @NotBlank
        @Size(max = 255)
        String description,

        @Schema(description = "Updated price of the work item", example = "109.99")
        @Positive
        float price
) {
}
