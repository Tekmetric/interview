package com.interview.business.services.reviews.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;

public record ReviewsPaging(
        @Nullable
        @Min(0)
        Integer page,

        @Nullable
        @Min(10)
        Integer size
) {
    public Integer page() {
        return page != null ? page : 0;
    }

    public Integer size() {
        return size != null ? size : 10;
    }
}
