package com.interview.runningevents.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SortDirectionTest {

    @Test
    void shouldReturnAscForNullInput() {
        assertThat(SortDirection.fromString(null)).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldParseValidValuesSuccessfully() {
        assertThat(SortDirection.fromString("ASC")).isEqualTo(SortDirection.ASC);
        assertThat(SortDirection.fromString("asc")).isEqualTo(SortDirection.ASC);
        assertThat(SortDirection.fromString("Asc")).isEqualTo(SortDirection.ASC);

        assertThat(SortDirection.fromString("DESC")).isEqualTo(SortDirection.DESC);
        assertThat(SortDirection.fromString("desc")).isEqualTo(SortDirection.DESC);
        assertThat(SortDirection.fromString("Desc")).isEqualTo(SortDirection.DESC);
    }

    @Test
    void shouldReturnAscForInvalidInput() {
        assertThat(SortDirection.fromString("invalid")).isEqualTo(SortDirection.ASC);
        assertThat(SortDirection.fromString("123")).isEqualTo(SortDirection.ASC);
        assertThat(SortDirection.fromString("")).isEqualTo(SortDirection.ASC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "asc", "Asc", "DESC", "desc", "Desc"})
    void shouldValidateValidSortDirections(String direction) {
        assertThat(SortDirection.isValid(direction)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "123", "", "ascending", "descending"})
    void shouldInvalidateInvalidSortDirections(String direction) {
        assertThat(SortDirection.isValid(direction)).isFalse();
    }

    @Test
    void shouldInvalidateNullSortDirection() {
        assertThat(SortDirection.isValid(null)).isFalse();
    }
}
