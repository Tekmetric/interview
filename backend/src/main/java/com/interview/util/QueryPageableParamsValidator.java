package com.interview.util;

import org.springframework.data.domain.Pageable;

public class QueryPageableParamsValidator {

    private static final int MAX_PAGE_SIZE = 500;

    public static void validate(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size cannot be greater than " + MAX_PAGE_SIZE);
        }
    }
}
