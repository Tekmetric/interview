package com.interview.runningevents.infrastructure.web;

import java.util.HashSet;
import java.util.Set;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.model.SortDirection;

/**
 * Utility class for validating API query parameters.
 */
public class QueryParamValidator {

    /**
     * Valid field names that can be used for sorting.
     * Limiting to only id, name, and dateTime for simplicity.
     */
    private static final Set<String> VALID_SORT_FIELDS = new HashSet<>();

    static {
        VALID_SORT_FIELDS.add("id");
        VALID_SORT_FIELDS.add("name");
        VALID_SORT_FIELDS.add("dateTime");
    }

    /**
     * Validates sort field parameter.
     *
     * @param sortBy The field name to sort by
     * @throws ValidationException if the sort field is not valid
     */
    public static void validateSortField(String sortBy) {
        if (sortBy != null && !VALID_SORT_FIELDS.contains(sortBy)) {
            throw new ValidationException("Invalid sort field: " + sortBy + ". Valid fields are: id, name, dateTime");
        }
    }

    /**
     * Validates sort direction parameter.
     *
     * @param sortDir The sort direction string
     * @throws ValidationException if the sort direction is not valid
     */
    public static void validateSortDirection(String sortDir) {
        if (sortDir != null && !SortDirection.isValid(sortDir)) {
            throw new ValidationException("Invalid sort direction. Must be ASC or DESC.");
        }
    }
}
