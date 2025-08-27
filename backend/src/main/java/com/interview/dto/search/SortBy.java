package com.interview.dto.search;


import jakarta.validation.constraints.NotNull;

public record SortBy(
        @NotNull FieldName fieldName,
        @NotNull Direction direction) {

    public static SortBy of(FieldName fieldName, Direction direction) {
        return new SortBy(fieldName, direction);
    }
}
