package com.interview.runningevents.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.domain.model.RunningEvent;

public class RunningEventRepositoryAdapterTest {

    private RunningEventJpaRepository jpaRepository;
    private RunningEventRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(RunningEventJpaRepository.class);
        adapter = new RunningEventRepositoryAdapter(jpaRepository);
    }

    @Test
    void shouldSaveRunningEvent() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent domainEvent = RunningEvent.builder()
                .name("Test Event")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        RunningEventEntity entity = RunningEventMapper.toEntity(domainEvent);
        RunningEventEntity savedEntity = RunningEventEntity.builder()
                .id(1L)
                .name("Test Event")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        when(jpaRepository.save(any(RunningEventEntity.class))).thenReturn(savedEntity);

        // When
        RunningEvent result = adapter.save(domainEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        ArgumentCaptor<RunningEventEntity> entityCaptor = ArgumentCaptor.forClass(RunningEventEntity.class);
        verify(jpaRepository, times(1)).save(entityCaptor.capture());

        RunningEventEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getName()).isEqualTo("Test Event");
        assertThat(capturedEntity.getDateTime()).isEqualTo(futureTime);
        assertThat(capturedEntity.getLocation()).isEqualTo("Test Location");
    }

    @Test
    void shouldThrowExceptionWhenSavingNullEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> adapter.save(null));
        verify(jpaRepository, never()).save(any());
    }

    @Test
    void shouldFindRunningEventById() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity = RunningEventEntity.builder()
                .id(1L)
                .name("Test Event")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // When
        Optional<RunningEvent> result = adapter.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Test Event");

        verify(jpaRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyOptionalWhenEventNotFound() {
        // Given
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<RunningEvent> result = adapter.findById(99L);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRepository, times(1)).findById(99L);
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> adapter.findById(null));
        verify(jpaRepository, never()).findById(any());
    }

    @Test
    void shouldFindAllRunningEvents() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity1 = RunningEventEntity.builder()
                .id(1L)
                .name("Event 1")
                .dateTime(futureTime)
                .location("Location 1")
                .build();

        RunningEventEntity entity2 = RunningEventEntity.builder()
                .id(2L)
                .name("Event 2")
                .dateTime(futureTime + 1000)
                .location("Location 2")
                .build();

        List<RunningEventEntity> entities = List.of(entity1, entity2);
        Page<RunningEventEntity> page = new PageImpl<>(entities, PageRequest.of(0, 10), 2);

        RunningEventQuery query =
                RunningEventQuery.builder().page(0).pageSize(10).build();

        when(jpaRepository.findAllByOrderByDateTime(any(Pageable.class))).thenReturn(page);

        // When
        PaginatedResult<RunningEvent> result = adapter.findAll(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);

        // Verify the correct pagination was used
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository, times(1)).findAllByOrderByDateTime(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
    }

    @Test
    void shouldFindRunningEventsWithDateFilter() {
        // Given
        Long fromDate = Instant.now().toEpochMilli();
        Long toDate = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventQuery query = RunningEventQuery.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .page(0)
                .pageSize(10)
                .build();

        Page<RunningEventEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(jpaRepository.findByDateTimeBetweenOrderByDateTime(fromDate, toDate, PageRequest.of(0, 10)))
                .thenReturn(emptyPage);

        // When
        PaginatedResult<RunningEvent> result = adapter.findAll(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();

        // Verify the correct parameters were passed
        ArgumentCaptor<Long> fromDateCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> toDateCaptor = ArgumentCaptor.forClass(Long.class);

        verify(jpaRepository, times(1))
                .findByDateTimeBetweenOrderByDateTime(
                        fromDateCaptor.capture(), toDateCaptor.capture(), any(Pageable.class));

        assertThat(fromDateCaptor.getValue()).isEqualTo(fromDate);
        assertThat(toDateCaptor.getValue()).isEqualTo(toDate);
    }

    @Test
    void shouldThrowExceptionWhenFindingAllWithNullQuery() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> adapter.findAll(null));
        verify(jpaRepository, never()).findAllByOrderByDateTime(any());
        verify(jpaRepository, never()).findByDateTimeBetweenOrderByDateTime(any(), any(), any());
    }

    @Test
    void shouldDeleteRunningEventById() {
        // Given
        when(jpaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = adapter.deleteById(1L);

        // Then
        assertThat(result).isTrue();
        verify(jpaRepository, times(1)).existsById(1L);
        verify(jpaRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentEvent() {
        // Given
        when(jpaRepository.existsById(99L)).thenReturn(false);

        // When
        boolean result = adapter.deleteById(99L);

        // Then
        assertThat(result).isFalse();
        verify(jpaRepository, times(1)).existsById(99L);
        verify(jpaRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithNullId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> adapter.deleteById(null));
        verify(jpaRepository, never()).existsById(any());
        verify(jpaRepository, never()).deleteById(any());
    }

    @Test
    void shouldCheckIfRunningEventExists() {
        // Given
        when(jpaRepository.existsById(1L)).thenReturn(true);
        when(jpaRepository.existsById(99L)).thenReturn(false);

        // When/Then
        assertThat(adapter.existsById(1L)).isTrue();
        assertThat(adapter.existsById(99L)).isFalse();

        verify(jpaRepository, times(1)).existsById(1L);
        verify(jpaRepository, times(1)).existsById(99L);
    }

    @Test
    void shouldThrowExceptionWhenCheckingExistenceWithNullId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> adapter.existsById(null));
        verify(jpaRepository, never()).existsById(any());
    }
}
