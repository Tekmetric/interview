package com.interview.business.services.reviews.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record ReviewCreateRequest(
        @NotNull
        @Range(min = 1, max = 5)
        Integer rating,

        @NotNull
        @Size(min = 1, max = 2000)
        String message
) {
}
