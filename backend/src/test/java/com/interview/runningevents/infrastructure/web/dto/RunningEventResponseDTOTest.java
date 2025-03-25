package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.interview.runningevents.infrastructure.web.util.DateTimeConverter;

public class RunningEventResponseDTOTest {

    @Test
    public void shouldCreateValidDTO() {
        // Given
        String futureTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        RunningEventResponseDTO dto = RunningEventResponseDTO.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getDateTime()).isEqualTo(futureTime);
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    public void shouldWorkWithPartialData() {
        // Given
        RunningEventResponseDTO dto = RunningEventResponseDTO.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(DateTimeConverter.fromTimestamp(Instant.now().toEpochMilli()))
                .location("Test Location")
                // No description or furtherInformation
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getFurtherInformation()).isNull();
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

        String dateTime = DateTimeConverter.fromTimestamp(123456789L);

        // When
        dto.setId(1L);
        dto.setName("Updated Name");
        dto.setDateTime(dateTime);
        dto.setLocation("Updated Location");
        dto.setDescription("Updated Description");

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Updated Name");
        assertThat(dto.getDateTime()).isEqualTo(dateTime);
        assertThat(dto.getLocation()).isEqualTo("Updated Location");
        assertThat(dto.getDescription()).isEqualTo("Updated Description");
    }
}
