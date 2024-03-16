package com.interview.business.services.recipes.dto;

import com.interview.business.domain.Recipe;
import com.interview.core.domain.BaseEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Sort;

public record RecipesSorting(
        @Nullable SortBy sortBy,
        @Nullable SortOrder sortOrder
) {

    public enum SortBy {
        CREATED_AT, RATING_AVERAGE, RATING_COUNT
    }

    public enum SortOrder {
        ASC, DESC
    }

    public Sort toSort() {
        Sort.Direction direction = switch (this.sortOrder != null ? this.sortOrder : SortOrder.DESC) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };

        String property = switch (this.sortBy != null ? this.sortBy : SortBy.CREATED_AT) {
            case CREATED_AT -> BaseEntity.Fields.createdAt;
            case RATING_AVERAGE -> Recipe.Fields.ratingAverage;
            case RATING_COUNT -> Recipe.Fields.ratingCount;
        };

        return Sort.by(direction, property);
    }
}
