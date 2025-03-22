package com.interview.runningevents.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

@ExtendWith(MockitoExtension.class)
class RunningEventServiceTest {

    @Mock
    private RunningEventRepository runningEventRepository;

    private RunningEventService runningEventService;

    @BeforeEach
    void setUp() {
        runningEventService = new RunningEventService(runningEventRepository);
    }

    @Test
    void shouldCreateRunningEventSuccessfully() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent newEvent = RunningEvent.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .build();

        RunningEvent savedEvent = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .build();

        when(runningEventRepository.save(any(RunningEvent.class))).thenReturn(savedEvent);

        // When
        RunningEvent result = runningEventService.createRunningEvent(newEvent);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Marathon", result.getName());
        assertEquals(futureTime, result.getDateTime());
        assertEquals("Test Location", result.getLocation());
        assertEquals("Test Description", result.getDescription());

        verify(runningEventRepository, times(1)).save(newEvent);
    }

    @Test
    void shouldThrowExceptionWhenCreatingEventWithExistingId() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent eventWithId = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            runningEventService.createRunningEvent(eventWithId);
        });

        assertThat(exception.getMessage()).contains("ID must be null");
        verify(runningEventRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingInvalidEvent() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        // Create an event with name that exceeds max length (100 characters)
        String tooLongName = "X".repeat(101);

        RunningEvent invalidEvent = RunningEvent.builder()
                .name(tooLongName) // Name too long - invalid
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            runningEventService.createRunningEvent(invalidEvent);
        });

        assertThat(exception.getMessage()).contains("Invalid running event");
        verify(runningEventRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingNullEvent() {
        // When/Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            runningEventService.createRunningEvent(null);
        });

        assertThat(exception.getMessage()).contains("cannot be null");
        verify(runningEventRepository, never()).save(any());
    }

    @Test
    void shouldGetRunningEventByIdSuccessfully() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent event = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .build();

        when(runningEventRepository.findById(1L)).thenReturn(Optional.of(event));

        // When
        Optional<RunningEvent> result = runningEventService.getRunningEventById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Marathon", result.get().getName());

        verify(runningEventRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyOptionalWhenEventNotFound() {
        // Given
        when(runningEventRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<RunningEvent> result = runningEventService.getRunningEventById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(runningEventRepository, times(1)).findById(99L);
    }

    @Test
    void shouldThrowExceptionWhenGettingEventWithNullId() {
        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            runningEventService.getRunningEventById(null);
        });

        assertThat(exception.getMessage()).contains("ID cannot be null");
        verify(runningEventRepository, never()).findById(any());
    }

    // List Running Events Tests

    @Test
    void shouldListRunningEventsSuccessfully() {
        // Given
        RunningEventQuery query =
                RunningEventQuery.builder().page(0).pageSize(10).build();

        List<RunningEvent> events = Arrays.asList(
                RunningEvent.builder()
                        .id(1L)
                        .name("Event 1")
                        .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                        .location("Location 1")
                        .build(),
                RunningEvent.builder()
                        .id(2L)
                        .name("Event 2")
                        .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                        .location("Location 2")
                        .build());

        PaginatedResult<RunningEvent> expectedResult = PaginatedResult.of(events, 2, 0, 10);

        when(runningEventRepository.findAll(query)).thenReturn(expectedResult);

        // When
        PaginatedResult<RunningEvent> result = runningEventService.listRunningEvents(query);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getPageSize());

        verify(runningEventRepository, times(1)).findAll(query);
    }

    @Test
    void shouldReturnEmptyResultWhenNoEventsMatch() {
        // Given
        RunningEventQuery query = RunningEventQuery.builder()
                .fromDate(Instant.now().plus(100, ChronoUnit.DAYS).toEpochMilli())
                .toDate(Instant.now().plus(110, ChronoUnit.DAYS).toEpochMilli())
                .build();

        PaginatedResult<RunningEvent> emptyResult = PaginatedResult.of(Collections.emptyList(), 0, 0, 10);

        when(runningEventRepository.findAll(query)).thenReturn(emptyResult);

        // When
        PaginatedResult<RunningEvent> result = runningEventService.listRunningEvents(query);

        // Then
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotalItems());

        verify(runningEventRepository, times(1)).findAll(query);
    }

    @Test
    void shouldFilterEventsByDateRange() {
        // Given
        long fromDate = Instant.now().plus(5, ChronoUnit.DAYS).toEpochMilli();
        long toDate = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();

        RunningEventQuery query =
                RunningEventQuery.builder().fromDate(fromDate).toDate(toDate).build();

        List<RunningEvent> events = Arrays.asList(RunningEvent.builder()
                .id(1L)
                .name("Event in Range")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location")
                .build());

        PaginatedResult<RunningEvent> expectedResult = PaginatedResult.of(events, 1, 0, 20);

        when(runningEventRepository.findAll(query)).thenReturn(expectedResult);

        // When
        PaginatedResult<RunningEvent> result = runningEventService.listRunningEvents(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Event in Range", result.getItems().get(0).getName());

        verify(runningEventRepository, times(1)).findAll(query);
    }

    @Test
    void shouldPaginateResults() {
        // Given
        RunningEventQuery firstPageQuery =
                RunningEventQuery.builder().page(0).pageSize(1).build();

        RunningEventQuery secondPageQuery =
                RunningEventQuery.builder().page(1).pageSize(1).build();

        List<RunningEvent> firstPageEvents = Collections.singletonList(RunningEvent.builder()
                .id(1L)
                .name("Event 1")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 1")
                .build());

        List<RunningEvent> secondPageEvents = Collections.singletonList(RunningEvent.builder()
                .id(2L)
                .name("Event 2")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Location 2")
                .build());

        PaginatedResult<RunningEvent> firstPageResult = PaginatedResult.of(firstPageEvents, 2, 0, 1);

        PaginatedResult<RunningEvent> secondPageResult = PaginatedResult.of(secondPageEvents, 2, 1, 1);

        when(runningEventRepository.findAll(firstPageQuery)).thenReturn(firstPageResult);
        when(runningEventRepository.findAll(secondPageQuery)).thenReturn(secondPageResult);

        // When
        PaginatedResult<RunningEvent> resultPage1 = runningEventService.listRunningEvents(firstPageQuery);
        PaginatedResult<RunningEvent> resultPage2 = runningEventService.listRunningEvents(secondPageQuery);

        // Then
        assertNotNull(resultPage1);
        assertEquals(1, resultPage1.getItems().size());
        assertEquals("Event 1", resultPage1.getItems().get(0).getName());
        assertEquals(0, resultPage1.getPage());
        assertTrue(resultPage1.isHasNext());
        assertFalse(resultPage1.isHasPrevious());

        assertNotNull(resultPage2);
        assertEquals(1, resultPage2.getItems().size());
        assertEquals("Event 2", resultPage2.getItems().get(0).getName());
        assertEquals(1, resultPage2.getPage());
        assertFalse(resultPage2.isHasNext());
        assertTrue(resultPage2.isHasPrevious());

        verify(runningEventRepository, times(1)).findAll(firstPageQuery);
        verify(runningEventRepository, times(1)).findAll(secondPageQuery);
    }

    @Test
    void shouldThrowExceptionForInvalidPaginationParameters() {
        // Given
        RunningEventQuery invalidPageQuery =
                RunningEventQuery.builder().page(-1).build();

        RunningEventQuery invalidSizeQuery =
                RunningEventQuery.builder().pageSize(0).build();

        // When/Then
        Exception pageException = assertThrows(IllegalArgumentException.class, () -> {
            runningEventService.listRunningEvents(invalidPageQuery);
        });

        Exception sizeException = assertThrows(IllegalArgumentException.class, () -> {
            runningEventService.listRunningEvents(invalidSizeQuery);
        });

        assertThat(pageException.getMessage()).contains("Page number cannot be negative");
        assertThat(sizeException.getMessage()).contains("Page size must be greater than zero");

        verify(runningEventRepository, never()).findAll(any());
    }

    @Test
    void shouldThrowExceptionForInvalidDateRange() {
        // Given
        long laterDate = Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli();
        long earlierDate = Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli();

        RunningEventQuery invalidQuery = RunningEventQuery.builder()
                .fromDate(laterDate)
                .toDate(earlierDate) // fromDate is after toDate - invalid
                .build();

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            runningEventService.listRunningEvents(invalidQuery);
        });

        assertThat(exception.getMessage()).contains("From date cannot be after to date");
        verify(runningEventRepository, never()).findAll(any());
    }

    @Test
    void shouldThrowExceptionForNullQuery() {
        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            runningEventService.listRunningEvents(null);
        });

        assertThat(exception.getMessage()).contains("Query cannot be null");
        verify(runningEventRepository, never()).findAll(any());
    }
}
