package com.interview.runningevents.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RunningEventQueryTest {

    @Test
    void shouldUseAscSortDirectionByDefault() {
        // When
        RunningEventQuery query = RunningEventQuery.builder().build();

        // Then
        assertThat(query.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldSetSortDirectionCorrectly() {
        // When
        RunningEventQuery ascQuery =
                RunningEventQuery.builder().sortDirection(SortDirection.ASC).build();

        RunningEventQuery descQuery =
                RunningEventQuery.builder().sortDirection(SortDirection.DESC).build();

        // Then
        assertThat(ascQuery.getSortDirection()).isEqualTo(SortDirection.ASC);
        assertThat(descQuery.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void shouldHandleSettersForSortDirection() {
        // Given
        RunningEventQuery query = RunningEventQuery.builder().build();
        assertThat(query.getSortDirection()).isEqualTo(SortDirection.ASC);

        // When
        query.setSortDirection(SortDirection.DESC);

        // Then
        assertThat(query.getSortDirection()).isEqualTo(SortDirection.DESC);
    }
}
