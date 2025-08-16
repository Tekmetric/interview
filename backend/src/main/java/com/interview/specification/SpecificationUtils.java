package com.interview.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

public class SpecificationUtils {

    public static <T> Specification<T> fuzzySearch(final String fieldName, final String searchValue) {
        if (searchValue == null || searchValue.trim().isEmpty()) {
            return null; // No constraint when search value is empty
        }
        return (root, cq, builder) -> {
            final String likePattern = "%" + searchValue.toLowerCase() + "%";
            return builder.like(builder.lower(root.get(fieldName)), likePattern);
        };
    }

    // Use or to connect a list of specs
    // If input is a list of all nulls, return null
    public static <T> Specification<T> orSpecs(List<Specification<T>> specs) {
        return specs.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::or)
                .orElse(null);
    }
}