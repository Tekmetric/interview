package com.interview.validator;

import com.interview.model.exception.FieldNotAllowedInSortException;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public class PaginationValidator {

    private PaginationValidator() {

    }

    public static void validate(Pageable pageable, Set<String> allowedFieldsToSortBy) {
        pageable.getSort().forEach(order -> {
            if (!allowedFieldsToSortBy.contains(order.getProperty())) {
                throw new FieldNotAllowedInSortException("Sorting by '" + order.getProperty() + "' is not allowed");
            }
        });
    }
}
