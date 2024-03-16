package com.interview.business.services.reviews.dto;

import com.interview.business.domain.Review;
import com.interview.core.domain.BaseEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Sort;

public record ReviewsSorting(
        @Nullable SortBy sortBy,
        @Nullable SortOrder sortOrder
) {

    public enum SortBy {
        CREATED_AT, RATING
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
            case RATING -> Review.Fields.rating;
        };

        return Sort.by(direction, property);
    }
}
