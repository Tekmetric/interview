package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class RunningEventQueryDTOTest {

    @Test
    public void shouldCreateWithAllFields() {
        // Given
        Long fromDate = Instant.now().toEpochMilli();
        Long toDate = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventQueryDTO dto = RunningEventQueryDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .page(2)
                .pageSize(15)
                .sortBy("name")
                .sortDirection("DESC")
                .build();

        // Then
        assertThat(dto.getFromDate()).isEqualTo(fromDate);
        assertThat(dto.getToDate()).isEqualTo(toDate);
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getPageSize()).isEqualTo(15);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getSortDirection()).isEqualTo("DESC");
    }

    @Test
    public void shouldCreateWithDefaultValues() {
        // Given
        RunningEventQueryDTO dto = RunningEventQueryDTO.builder().build();

        // Then
        assertThat(dto.getFromDate()).isNull();
        assertThat(dto.getToDate()).isNull();
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getPageSize()).isEqualTo(20);
        assertThat(dto.getSortBy()).isEqualTo("dateTime");
        assertThat(dto.getSortDirection()).isEqualTo("ASC");
    }

    @Test
    public void shouldOverrideDefaultValues() {
        // Given
        RunningEventQueryDTO dto = RunningEventQueryDTO.builder()
                .page(5)
                .pageSize(10)
                .sortBy("location")
                .sortDirection("DESC")
                .build();

        // Then
        assertThat(dto.getPage()).isEqualTo(5);
        assertThat(dto.getPageSize()).isEqualTo(10);
        assertThat(dto.getSortBy()).isEqualTo("location");
        assertThat(dto.getSortDirection()).isEqualTo("DESC");
    }

    @Test
    public void shouldSupportNoArgsConstructor() {
        // Given
        RunningEventQueryDTO dto = new RunningEventQueryDTO();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getFromDate()).isNull();
        assertThat(dto.getToDate()).isNull();
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.getPageSize()).isEqualTo(20);
        assertThat(dto.getSortBy()).isEqualTo("dateTime");
        assertThat(dto.getSortDirection()).isEqualTo("ASC");
    }

    @Test
    public void shouldSupportSetters() {
        // Given
        RunningEventQueryDTO dto = new RunningEventQueryDTO();
        Long fromDate = Instant.now().toEpochMilli();
        Long toDate = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        // When
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        dto.setPage(3);
        dto.setPageSize(25);
        dto.setSortBy("description");
        dto.setSortDirection("DESC");

        // Then
        assertThat(dto.getFromDate()).isEqualTo(fromDate);
        assertThat(dto.getToDate()).isEqualTo(toDate);
        assertThat(dto.getPage()).isEqualTo(3);
        assertThat(dto.getPageSize()).isEqualTo(25);
        assertThat(dto.getSortBy()).isEqualTo("description");
        assertThat(dto.getSortDirection()).isEqualTo("DESC");
    }

    @Test
    public void shouldUseAllArgsConstructorWithSortDirection() {
        // Given
        Long fromDate = Instant.now().toEpochMilli();
        Long toDate = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        // When
        RunningEventQueryDTO dto = new RunningEventQueryDTO(fromDate, toDate, 1, 15, "name", "DESC");

        // Then
        assertThat(dto.getFromDate()).isEqualTo(fromDate);
        assertThat(dto.getToDate()).isEqualTo(toDate);
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getPageSize()).isEqualTo(15);
        assertThat(dto.getSortBy()).isEqualTo("name");
        assertThat(dto.getSortDirection()).isEqualTo("DESC");
    }
}
