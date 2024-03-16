package com.interview.business.services.recipes.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;

public record RecipesPaging(
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
        return size != null ? size : 20;
    }
}
