package com.interview.runningevents.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interview.runningevents.application.exception.ValidationException;
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
    void shouldThrowExceptionWhenCreatingEventWithSetId() {
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
}
