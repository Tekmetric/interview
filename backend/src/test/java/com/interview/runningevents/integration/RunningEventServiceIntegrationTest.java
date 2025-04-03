package com.interview.runningevents.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.DeleteRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.in.UpdateRunningEventUseCase;
import com.interview.runningevents.domain.model.RunningEvent;

@RunningEventIntegrationTestConfig
public class RunningEventServiceIntegrationTest {

    @Autowired
    private CreateRunningEventUseCase createRunningEventUseCase;

    @Autowired
    private GetRunningEventUseCase getRunningEventUseCase;

    @Autowired
    private ListRunningEventsUseCase listRunningEventsUseCase;

    @Autowired
    private UpdateRunningEventUseCase updateRunningEventUseCase;

    @Autowired
    private DeleteRunningEventUseCase deleteRunningEventUseCase;

    @Test
    public void shouldCreateAndRetrieveRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Service Test Event");

        // When
        RunningEvent createdEvent = createRunningEventUseCase.createRunningEvent(event);
        Optional<RunningEvent> retrievedEvent = getRunningEventUseCase.getRunningEventById(createdEvent.getId());

        // Then
        assertThat(createdEvent.getId()).isNotNull();
        assertThat(retrievedEvent).isPresent();
        assertThat(retrievedEvent.get().getName()).isEqualTo("Service Test Event");
        assertThat(retrievedEvent.get().getLocation()).isEqualTo("Test Location");
    }

    @Test
    public void shouldUpdateRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Original Name");
        RunningEvent createdEvent = createRunningEventUseCase.createRunningEvent(event);

        // When
        createdEvent.setName("Updated Name");
        createdEvent.setLocation("Updated Location");
        Optional<RunningEvent> updatedEvent = updateRunningEventUseCase.updateRunningEvent(createdEvent);

        // Then
        assertThat(updatedEvent).isPresent();
        assertThat(updatedEvent.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedEvent.get().getLocation()).isEqualTo("Updated Location");

        // Verify the update was persisted
        Optional<RunningEvent> retrievedEvent = getRunningEventUseCase.getRunningEventById(createdEvent.getId());
        assertThat(retrievedEvent).isPresent();
        assertThat(retrievedEvent.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    public void shouldDeleteRunningEvent() {
        // Given
        RunningEvent event = createTestEvent("Event To Delete");
        RunningEvent createdEvent = createRunningEventUseCase.createRunningEvent(event);

        // When
        boolean deleted = deleteRunningEventUseCase.deleteRunningEvent(createdEvent.getId());
        Optional<RunningEvent> retrievedEvent = getRunningEventUseCase.getRunningEventById(createdEvent.getId());

        // Then
        assertThat(deleted).isTrue();
        assertThat(retrievedEvent).isEmpty();
    }

    @Test
    public void shouldListRunningEvents() {
        // Given
        for (int i = 0; i < 5; i++) {
            RunningEvent event = createTestEvent("List Test Event " + i);
            createRunningEventUseCase.createRunningEvent(event);
        }

        // When
        RunningEventQuery query =
                RunningEventQuery.builder().page(0).pageSize(10).build();
        PaginatedResult<RunningEvent> result = listRunningEventsUseCase.listRunningEvents(query);

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
        createRunningEventUseCase.createRunningEvent(nearFutureEvent);

        // Create event in far future
        RunningEvent farFutureEvent = createTestEventWithDate("Far Future Event", now.plus(60, ChronoUnit.DAYS));
        createRunningEventUseCase.createRunningEvent(farFutureEvent);

        // When - query for events in near future only
        RunningEventQuery query = RunningEventQuery.builder()
                .fromDate(now.toEpochMilli())
                .toDate(now.plus(30, ChronoUnit.DAYS).toEpochMilli())
                .build();

        PaginatedResult<RunningEvent> result = listRunningEventsUseCase.listRunningEvents(query);

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
        createRunningEventUseCase.createRunningEvent(createTestEvent("Event A"));
        createRunningEventUseCase.createRunningEvent(createTestEvent("Event C"));
        createRunningEventUseCase.createRunningEvent(createTestEvent("Event B"));

        // When - sort ascending by name
        RunningEventQuery ascQuery = RunningEventQuery.builder()
                .sortBy("name")
                .sortDirection(SortDirection.ASC)
                .build();

        PaginatedResult<RunningEvent> ascResult = listRunningEventsUseCase.listRunningEvents(ascQuery);

        // Then
        assertThat(ascResult.getItems()).isNotEmpty();
        assertThat(ascResult.getItems().get(0).getName()).containsIgnoringCase("A");

        // When - sort descending by name
        RunningEventQuery descQuery = RunningEventQuery.builder()
                .sortBy("name")
                .sortDirection(SortDirection.DESC)
                .build();

        PaginatedResult<RunningEvent> descResult = listRunningEventsUseCase.listRunningEvents(descQuery);

        // Then
        assertThat(descResult.getItems()).isNotEmpty();
        assertThat(descResult.getItems().get(0).getName()).containsIgnoringCase("C");
    }

    @Test
    public void shouldPaginateResults() {
        // Given
        for (int i = 0; i < 10; i++) {
            RunningEvent event = createTestEvent("Pagination Event " + i);
            createRunningEventUseCase.createRunningEvent(event);
        }

        // When - first page
        RunningEventQuery firstPageQuery =
                RunningEventQuery.builder().page(0).pageSize(5).build();

        PaginatedResult<RunningEvent> firstPageResult = listRunningEventsUseCase.listRunningEvents(firstPageQuery);

        // Then
        assertThat(firstPageResult.getItems()).hasSize(5);
        assertThat(firstPageResult.getPage()).isEqualTo(0);
        assertThat(firstPageResult.getPageSize()).isEqualTo(5);
        assertThat(firstPageResult.isHasNext()).isTrue();

        // When - second page
        RunningEventQuery secondPageQuery =
                RunningEventQuery.builder().page(1).pageSize(5).build();

        PaginatedResult<RunningEvent> secondPageResult = listRunningEventsUseCase.listRunningEvents(secondPageQuery);

        // Then
        assertThat(secondPageResult.getItems()).isNotEmpty();
        assertThat(secondPageResult.getPage()).isEqualTo(1);
        assertThat(secondPageResult.isHasPrevious()).isTrue();
    }

    @Test
    public void shouldValidateRunningEvent() {
        // Given - event with null name (required field)
        RunningEvent invalidEvent = RunningEvent.builder()
                .name(null)
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Test Location")
                .build();

        // When & Then
        assertThatThrownBy(() -> createRunningEventUseCase.createRunningEvent(invalidEvent))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid running event");

        // Given - event with empty name
        RunningEvent emptyNameEvent = RunningEvent.builder()
                .name("")
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Test Location")
                .build();

        // When & Then
        assertThatThrownBy(() -> createRunningEventUseCase.createRunningEvent(emptyNameEvent))
                .isInstanceOf(ValidationException.class);

        // Given - event with too long name
        RunningEvent longNameEvent = RunningEvent.builder()
                .name("A".repeat(101)) // Exceeds maximum 100 characters
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Test Location")
                .build();

        // When & Then
        assertThatThrownBy(() -> createRunningEventUseCase.createRunningEvent(longNameEvent))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void shouldRejectEventWithNullId() {
        // Given
        RunningEvent event = createTestEvent("Test Event");
        RunningEvent createdEvent = createRunningEventUseCase.createRunningEvent(event);
        createdEvent.setId(null);

        // When & Then
        assertThatThrownBy(() -> updateRunningEventUseCase.updateRunningEvent(createdEvent))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    public void shouldReturnEmptyWhenUpdatingNonExistentEvent() {
        // Given
        RunningEvent event = RunningEvent.builder()
                .id(999999L) // Non-existent ID
                .name("Test Event")
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Test Location")
                .build();

        // When
        Optional<RunningEvent> result = updateRunningEventUseCase.updateRunningEvent(event);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentEvent() {
        // When
        boolean result = deleteRunningEventUseCase.deleteRunningEvent(999999L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldThrowExceptionForInvalidQuery() {
        // Given - negative page number
        RunningEventQuery invalidPageQuery =
                RunningEventQuery.builder().page(-1).build();

        // When & Then
        assertThatThrownBy(() -> listRunningEventsUseCase.listRunningEvents(invalidPageQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page number cannot be negative");

        // Given - invalid page size
        RunningEventQuery invalidSizeQuery =
                RunningEventQuery.builder().pageSize(0).build();

        // When & Then
        assertThatThrownBy(() -> listRunningEventsUseCase.listRunningEvents(invalidSizeQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be greater than zero");

        // Given - fromDate after toDate
        RunningEventQuery invalidDateRangeQuery = RunningEventQuery.builder()
                .fromDate(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .toDate(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .build();

        // When & Then
        assertThatThrownBy(() -> listRunningEventsUseCase.listRunningEvents(invalidDateRangeQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("From date cannot be after to date");
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
