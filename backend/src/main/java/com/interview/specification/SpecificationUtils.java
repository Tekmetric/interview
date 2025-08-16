package com.interview.specification;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtils {

    public static <T> Specification<T> fuzzySearch(final String fieldName, final String searchValue) {
        return (root, cq, builder) -> {
            if (searchValue == null || searchValue.trim().isEmpty()) {
                return builder.conjunction(); // always true, returns all
            }
            final String likePattern = "%" + searchValue.toLowerCase() + "%";
            return builder.like(builder.lower(root.get(fieldName)), likePattern);
        };
    }
}