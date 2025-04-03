package com.interview.runningevents.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.interview.runningevents.application.exception.ValidationException;

class QueryParamValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"id", "name", "dateTime"})
    void shouldAcceptValidSortFields(String field) {
        // When/Then
        assertThatCode(() -> QueryParamValidator.validateSortField(field)).doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptNullSortField() {
        // When/Then - null is valid and will use default in the service
        assertThatCode(() -> QueryParamValidator.validateSortField(null)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "invalidField",
                "location",
                "description",
                "furtherInformation",
                "date",
                "event_name",
                "123",
                "unknown"
            })
    void shouldRejectInvalidSortFields(String field) {
        // When/Then
        assertThatThrownBy(() -> QueryParamValidator.validateSortField(field))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort field")
                .hasMessageContaining(field);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC", "asc", "desc", "Asc", "Desc"})
    void shouldAcceptValidSortDirections(String direction) {
        // When/Then
        assertThatCode(() -> QueryParamValidator.validateSortDirection(direction))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptNullSortDirection() {
        // When/Then - null is valid and will use default in the service
        assertThatCode(() -> QueryParamValidator.validateSortDirection(null)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASCENDING", "DESCENDING", "up", "down", "invalid", "123"})
    void shouldRejectInvalidSortDirections(String direction) {
        // When/Then
        assertThatThrownBy(() -> QueryParamValidator.validateSortDirection(direction))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort direction");
    }
}
