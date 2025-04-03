package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.util.DateTimeConverter;

public class RunningEventDTOMapperTest {

    private RunningEventDTOMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new RunningEventDTOMapper();
    }

    @Test
    public void shouldMapDomainToResponseDTO() {
        // Given
        Instant eventTime = Instant.now().plus(30, ChronoUnit.DAYS);

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
        assertThat(dto.getDateTime()).isEqualTo(DateTimeConverter.fromTimestamp(eventTime.toEpochMilli()));
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getFurtherInformation()).isEqualTo("Further Information");
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
    }

    @Test
    public void shouldMapRequestDTOToDomain() {
        // Given
        String futureTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

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
        assertThat(domain.getDateTime()).isEqualTo(DateTimeConverter.toTimestamp(futureTime));
        assertThat(domain.getLocation()).isEqualTo("Test Location");
        assertThat(domain.getDescription()).isEqualTo("Test Description");
        assertThat(domain.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    public void shouldUpdateDomainFromDTO() {
        // Given
        String futureTime = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

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
        assertThat(updatedEvent.getDateTime()).isEqualTo(DateTimeConverter.toTimestamp(futureTime));
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
        String fromDate = DateTimeConverter.fromTimestamp(Instant.now().toEpochMilli());
        String toDate = DateTimeConverter.fromTimestamp(
                Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

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
        assertThat(queryModel.getFromDate()).isEqualTo(DateTimeConverter.toTimestamp(fromDate));
        assertThat(queryModel.getToDate()).isEqualTo(DateTimeConverter.toTimestamp(toDate));
        assertThat(queryModel.getPage()).isEqualTo(2);
        assertThat(queryModel.getPageSize()).isEqualTo(15);
        assertThat(queryModel.getSortBy()).isEqualTo("name");
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @Test
    public void shouldProvideDefaultValuesForQueryDTOWithNulls() {
        // Given
        RunningEventQueryDTO queryDTO = RunningEventQueryDTO.builder()
                .fromDate(DateTimeConverter.fromTimestamp(123456L))
                .toDate(DateTimeConverter.fromTimestamp(789101L))
                // Null page, pageSize, sortBy, and sortDirection
                .build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel).isNotNull();
        assertThat(queryModel.getFromDate()).isEqualTo(120000L);
        assertThat(queryModel.getToDate()).isEqualTo(780000L);
        assertThat(queryModel.getPage()).isEqualTo(0); // Default value
        assertThat(queryModel.getPageSize()).isEqualTo(20); // Default value
        assertThat(queryModel.getSortBy()).isEqualTo("dateTime"); // Default value
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.ASC); // Default value
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

    @Test
    void shouldMapSortDirectionAsc() {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection("ASC").build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldMapSortDirectionDesc() {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection("DESC").build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "Asc", "ASC"})
    void shouldMapCaseInsensitiveAscValues(String direction) {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection(direction).build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"desc", "Desc", "DESC"})
    void shouldMapCaseInsensitiveDescValues(String direction) {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection(direction).build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "ASCENDING", "DESCENDING", "", "123"})
    void shouldMapInvalidValuesToAsc(String direction) {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection(direction).build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldMapNullValueToAsc() {
        // Given
        RunningEventQueryDTO queryDTO =
                RunningEventQueryDTO.builder().sortDirection(null).build();

        // When
        RunningEventQuery queryModel = mapper.toQueryModel(queryDTO);

        // Then
        assertThat(queryModel.getSortDirection()).isEqualTo(SortDirection.ASC);
    }
}
