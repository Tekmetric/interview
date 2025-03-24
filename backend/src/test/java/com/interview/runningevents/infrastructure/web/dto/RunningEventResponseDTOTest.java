package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class RunningEventResponseDTOTest {

    @Test
    public void shouldCreateValidDTO() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventResponseDTO dto = RunningEventResponseDTO.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .formattedDateTime("2023-05-01 10:00")
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getDateTime()).isEqualTo(futureTime);
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getFurtherInformation()).isEqualTo("Further Information");
        assertThat(dto.getFormattedDateTime()).isEqualTo("2023-05-01 10:00");
    }

    @Test
    public void shouldWorkWithPartialData() {
        // Given
        RunningEventResponseDTO dto = RunningEventResponseDTO.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(Instant.now().toEpochMilli())
                .location("Test Location")
                // No description, furtherInformation, or formattedDateTime
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getFurtherInformation()).isNull();
        assertThat(dto.getFormattedDateTime()).isNull();
    }

    @Test
    public void shouldSupportNoArgsConstructor() {
        // Given
        RunningEventResponseDTO dto = new RunningEventResponseDTO();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDateTime()).isNull();
        assertThat(dto.getLocation()).isNull();
    }

    @Test
    public void shouldSupportSetters() {
        // Given
        RunningEventResponseDTO dto = new RunningEventResponseDTO();

        // When
        dto.setId(1L);
        dto.setName("Updated Name");
        dto.setDateTime(123456789L);
        dto.setLocation("Updated Location");
        dto.setDescription("Updated Description");
        dto.setFormattedDateTime("2023-06-15 15:30");

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Updated Name");
        assertThat(dto.getDateTime()).isEqualTo(123456789L);
        assertThat(dto.getLocation()).isEqualTo("Updated Location");
        assertThat(dto.getDescription()).isEqualTo("Updated Description");
        assertThat(dto.getFormattedDateTime()).isEqualTo("2023-06-15 15:30");
    }
}
