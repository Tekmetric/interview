package com.interview.runningevents.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

@RunningEventIntegrationTestConfig
public class RunningEventRepositoryIntegrationTest {

    @Autowired
    private RunningEventRepository repository;

    @Test
    public void shouldSaveAndFindRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Repository Test Event");

        // When
        RunningEvent savedEvent = repository.save(event);
        Optional<RunningEvent> foundEvent = repository.findById(savedEvent.getId());

        // Then
        assertThat(savedEvent.getId()).isNotNull();
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getName()).isEqualTo("Repository Test Event");
        assertThat(foundEvent.get().getLocation()).isEqualTo("Test Location");
    }

    @Test
    public void shouldUpdateRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Original Name");
        RunningEvent savedEvent = repository.save(event);

        // When
        savedEvent.setName("Updated Name");
        savedEvent.setLocation("Updated Location");
        RunningEvent updatedEvent = repository.save(savedEvent);

        // Then
        assertThat(updatedEvent.getName()).isEqualTo("Updated Name");
        assertThat(updatedEvent.getLocation()).isEqualTo("Updated Location");

        // Verify the update was persisted
        Optional<RunningEvent> foundEvent = repository.findById(savedEvent.getId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    public void shouldDeleteRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Event To Delete");
        RunningEvent savedEvent = repository.save(event);

        // When
        boolean exists = repository.existsById(savedEvent.getId());
        boolean deleted = repository.deleteById(savedEvent.getId());
        boolean existsAfterDelete = repository.existsById(savedEvent.getId());

        // Then
        assertThat(exists).isTrue();
        assertThat(deleted).isTrue();
        assertThat(existsAfterDelete).isFalse();
    }

    @Test
    public void shouldFindAllRunningEvents() {
        // Given
        for (int i = 0; i < 5; i++) {
            RunningEvent event = createTestEvent("Repo Test Event " + i);
            repository.save(event);
        }

        // When
        RunningEventQuery query =
                RunningEventQuery.builder().page(0).pageSize(10).build();
        PaginatedResult<RunningEvent> result = repository.findAll(query);

        // Then
        assertThat(result.getItems()).isNotEmpty();
        assertThat(result.getTotalItems()).isGreaterThanOrEqualTo(5);
    }

    @Test
    public void shouldFilterRunningEventsByDateRange() {
        // Given
        Instant now = Instant.now();

        // Create event in near future
        RunningEvent nearFutureEvent = createTestEventWithDate("Near Future Event", now.plus(10, ChronoUnit.DAYS));
        repository.save(nearFutureEvent);

        // Create event in far future
        RunningEvent farFutureEvent = createTestEventWithDate("Far Future Event", now.plus(60, ChronoUnit.DAYS));
        repository.save(farFutureEvent);

        // When - query for events in near future only
        RunningEventQuery query = RunningEventQuery.builder()
                .fromDate(now.toEpochMilli())
                .toDate(now.plus(30, ChronoUnit.DAYS).toEpochMilli())
                .build();

        PaginatedResult<RunningEvent> result = repository.findAll(query);

        // Then
        assertThat(result.getItems()).isNotEmpty();
        assertThat(result.getItems().stream().anyMatch(e -> e.getName().equals("Near Future Event")))
                .isTrue();
        assertThat(result.getItems().stream().noneMatch(e -> e.getName().equals("Far Future Event")))
                .isTrue();
    }

    @Test
    public void shouldSortRunningEvents() {
        // Given
        repository.save(createTestEvent("Event A"));
        repository.save(createTestEvent("Event C"));
        repository.save(createTestEvent("Event B"));

        // When - sort ascending by name
        RunningEventQuery ascQuery = RunningEventQuery.builder()
                .sortBy("name")
                .sortDirection(SortDirection.ASC)
                .build();

        PaginatedResult<RunningEvent> ascResult = repository.findAll(ascQuery);

        // Then
        assertThat(ascResult.getItems()).isNotEmpty();
        assertThat(ascResult.getItems().get(0).getName()).containsIgnoringCase("A");

        // When - sort descending by name
        RunningEventQuery descQuery = RunningEventQuery.builder()
                .sortBy("name")
                .sortDirection(SortDirection.DESC)
                .build();

        PaginatedResult<RunningEvent> descResult = repository.findAll(descQuery);

        // Then
        assertThat(descResult.getItems()).isNotEmpty();
        assertThat(descResult.getItems().get(0).getName()).containsIgnoringCase("C");
    }

    @Test
    public void shouldPaginateResults() {
        // Given
        for (int i = 0; i < 10; i++) {
            RunningEvent event = createTestEvent("Pagination Event " + i);
            repository.save(event);
        }

        // When - first page
        RunningEventQuery firstPageQuery =
                RunningEventQuery.builder().page(0).pageSize(5).build();

        PaginatedResult<RunningEvent> firstPageResult = repository.findAll(firstPageQuery);

        // Then
        assertThat(firstPageResult.getItems()).hasSize(5);
        assertThat(firstPageResult.getPage()).isEqualTo(0);
        assertThat(firstPageResult.getPageSize()).isEqualTo(5);
        assertThat(firstPageResult.isHasNext()).isTrue();

        // When - second page
        RunningEventQuery secondPageQuery =
                RunningEventQuery.builder().page(1).pageSize(5).build();

        PaginatedResult<RunningEvent> secondPageResult = repository.findAll(secondPageQuery);

        // Then
        assertThat(secondPageResult.getItems()).isNotEmpty();
        assertThat(secondPageResult.getPage()).isEqualTo(1);
        assertThat(secondPageResult.isHasPrevious()).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentEvent() {
        // When
        boolean result = repository.deleteById(999999L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldCheckIfEventExists() {
        // Given
        RunningEvent event = createTestEvent("Exists Test Event");
        RunningEvent savedEvent = repository.save(event);

        // When
        boolean exists = repository.existsById(savedEvent.getId());
        boolean nonExistentExists = repository.existsById(999999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(nonExistentExists).isFalse();
    }

    @Test
    public void shouldValidateInputs() {
        // Given/When/Then - null ID for findById
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");

        // Given/When/Then - null ID for deleteById
        assertThatThrownBy(() -> repository.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");

        // Given/When/Then - null ID for existsById
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");

        // Given/When/Then - null event for save
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Running event cannot be null");

        // Given/When/Then - null query for findAll
        assertThatThrownBy(() -> repository.findAll(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Query cannot be null");
    }

    // Helper methods

    /**
     * Helper method to create a test event
     */
    private RunningEvent createTestEvent(String name) {
        return RunningEvent.builder()
                .name(name)
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Test Further Information")
                .build();
    }

    /**
     * Helper method to create a test event with specific date
     */
    private RunningEvent createTestEventWithDate(String name, Instant eventDate) {
        return RunningEvent.builder()
                .name(name)
                .dateTime(eventDate.toEpochMilli())
                .location("Test Location for " + name)
                .description("Test Description")
                .build();
    }
}
