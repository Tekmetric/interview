package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.domain.model.RunningEvent;

public class RunningEventDTOMapperTest {

    private RunningEventDTOMapper mapper;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    @BeforeEach
    public void setUp() {
        mapper = new RunningEventDTOMapper();
    }

    @Test
    public void shouldMapDomainToResponseDTO() {
        // Given
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);
        String expectedFormattedTime = DATE_FORMATTER.format(eventTime);

        RunningEvent event = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(eventTime.toEpochMilli())
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        RunningEventResponseDTO dto = mapper.toResponseDTO(event);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getDateTime()).isEqualTo(eventTime.toEpochMilli());
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getFurtherInformation()).isEqualTo("Further Information");
        assertThat(dto.getFormattedDateTime()).isEqualTo(expectedFormattedTime);
    }

    @Test
    public void shouldHandleNullDateTimeWhenFormattingDate() {
        // Given
        RunningEvent event = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(null) // Null dateTime
                .location("Test Location")
                .build();

        // When
        RunningEventResponseDTO dto = mapper.toResponseDTO(event);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getDateTime()).isNull();
        assertThat(dto.getFormattedDateTime()).isNull();
    }

    @Test
    public void shouldMapRequestDTOToDomain() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        RunningEvent domain = mapper.toDomain(requestDTO);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNull(); // ID should not be set when creating from DTO
        assertThat(domain.getName()).isEqualTo("Test Marathon");
        assertThat(domain.getDateTime()).isEqualTo(futureTime);
        assertThat(domain.getLocation()).isEqualTo("Test Location");
        assertThat(domain.getDescription()).isEqualTo("Test Description");
        assertThat(domain.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    public void shouldUpdateDomainFromDTO() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent existingEvent = RunningEvent.builder()
                .id(1L)
                .name("Original Name")
                .dateTime(Instant.now().toEpochMilli())
                .location("Original Location")
                .description("Original Description")
                .furtherInformation("Original Further Information")
                .build();

        RunningEventRequestDTO requestDTO = RunningEventRequestDTO.builder()
                .name("Updated Name")
                .dateTime(futureTime)
                .location("Updated Location")
                .description("Updated Description")
                .furtherInformation("Updated Further Information")
                .build();

        // When
        RunningEvent updatedEvent = mapper.updateDomainFromDTO(existingEvent, requestDTO);

        // Then
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getId()).isEqualTo(1L); // ID should be preserved
        assertThat(updatedEvent.getName()).isEqualTo("Updated Name");
        assertThat(updatedEvent.getDateTime()).isEqualTo(futureTime);
        assertThat(updatedEvent.getLocation()).isEqualTo("Updated Location");
        assertThat(updatedEvent.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedEvent.getFurtherInformation()).isEqualTo("Updated Further Information");
    }

    @Test
    public void shouldMapDomainListToResponseDTOList() {
        // Given
        RunningEvent event1 = RunningEvent.builder()
                .id(1L)
                .name("Event 1")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 1")
                .build();

        RunningEvent event2 = RunningEvent.builder()
                .id(2L)
                .name("Event 2")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 2")
                .build();

        List<RunningEvent> events = Arrays.asList(event1, event2);

        // When
        List<RunningEventResponseDTO> dtos = mapper.toResponseDTOList(events);

        // Then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
        assertThat(dtos.get(0).getName()).isEqualTo("Event 1");
        assertThat(dtos.get(1).getId()).isEqualTo(2L);
        assertThat(dtos.get(1).getName()).isEqualTo("Event 2");
    }

    @Test
    public void shouldMapQueryDTOToQueryModel() {
        // Given
        Long fromDate = Instant.now().toEpochMilli();
        Long toDate = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventQueryDTO queryDTO = RunningEventQueryDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .page(2)
                .pageSize(15)
                .sortBy("name")
                .sortDirection("DESC")
                .build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel).isNotNull();
        assertThat(queryModel.getFromDate()).isEqualTo(fromDate);
        assertThat(queryModel.getToDate()).isEqualTo(toDate);
        assertThat(queryModel.getPage()).isEqualTo(2);
        assertThat(queryModel.getPageSize()).isEqualTo(15);
        assertThat(queryModel.getSortBy()).isEqualTo("name");
        assertThat(queryModel.getSortDirection()).isEqualTo("DESC");
    }

    @Test
    public void shouldProvideDefaultValuesForQueryDTOWithNulls() {
        // Given
        RunningEventQueryDTO queryDTO = RunningEventQueryDTO.builder()
                .fromDate(123L)
                .toDate(456L)
                // Null page, pageSize, sortBy, and sortDirection
                .build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel).isNotNull();
        assertThat(queryModel.getFromDate()).isEqualTo(123L);
        assertThat(queryModel.getToDate()).isEqualTo(456L);
        assertThat(queryModel.getPage()).isEqualTo(0); // Default value
        assertThat(queryModel.getPageSize()).isEqualTo(20); // Default value
        assertThat(queryModel.getSortBy()).isEqualTo("dateTime"); // Default value
        assertThat(queryModel.getSortDirection()).isEqualTo("ASC"); // Default value
    }

    @Test
    public void shouldMapPaginatedResultToPaginatedResponseDTO() {
        // Given
        RunningEvent event1 = RunningEvent.builder()
                .id(1L)
                .name("Event 1")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 1")
                .build();

        RunningEvent event2 = RunningEvent.builder()
                .id(2L)
                .name("Event 2")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 2")
                .build();

        List<RunningEvent> events = Arrays.asList(event1, event2);
        PaginatedResult<RunningEvent> paginatedResult = PaginatedResult.of(events, 10, 1, 5);

        // When
        PaginatedResponseDTO<RunningEventResponseDTO> responseDTO = mapper.toPaginatedResponseDTO(paginatedResult);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getItems()).hasSize(2);
        assertThat(responseDTO.getTotalItems()).isEqualTo(10);
        assertThat(responseDTO.getPage()).isEqualTo(1);
        assertThat(responseDTO.getPageSize()).isEqualTo(5);
        assertThat(responseDTO.getTotalPages()).isEqualTo(2);
        assertThat(responseDTO.isHasPrevious()).isTrue();
        assertThat(responseDTO.isHasNext()).isFalse();

        // Check that the items are correctly mapped
        assertThat(responseDTO.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(responseDTO.getItems().get(0).getName()).isEqualTo("Event 1");
        assertThat(responseDTO.getItems().get(1).getId()).isEqualTo(2L);
        assertThat(responseDTO.getItems().get(1).getName()).isEqualTo("Event 2");
    }

    @Test
    public void shouldHandleNullInputs() {
        // Then
        assertThat(mapper.toResponseDTO(null)).isNull();
        assertThat(mapper.toResponseDTOList(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.updateDomainFromDTO(null, null)).isNull();
        assertThat(mapper.toQueryModel(null)).isNotNull(); // Should provide defaults
        assertThat(mapper.toPaginatedResponseDTO(null)).isNull();
    }
}
