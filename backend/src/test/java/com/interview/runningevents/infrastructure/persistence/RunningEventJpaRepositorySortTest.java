package com.interview.runningevents.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class RunningEventJpaRepositorySortTest {

    @Autowired
    private RunningEventJpaRepository repository;

    @BeforeEach
    void setup() {
        // Clear all existing data
        repository.deleteAll();

        // Insert test data with varying dates
        Long pastTime = Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli();
        Long presentTime = Instant.now().toEpochMilli();
        Long futureTime1 = Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli();
        Long futureTime2 = Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli();
        Long futureTime3 = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        repository.save(RunningEventEntity.builder()
                .name("Past Event")
                .dateTime(pastTime)
                .location("Past Location")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("Present Event")
                .dateTime(presentTime)
                .location("Present Location")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("Future Event 1")
                .dateTime(futureTime1)
                .location("Future Location 1")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("Future Event 2")
                .dateTime(futureTime2)
                .location("Future Location 2")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("Future Event 3")
                .dateTime(futureTime3)
                .location("Future Location 3")
                .build());
    }

    @Test
    public void shouldSortEventsAscendingByDateTime() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dateTime"));

        // When
        Page<RunningEventEntity> result = repository.findAll(pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then
        assertThat(events).hasSize(5);
        assertThat(events)
                .extracting("name")
                .containsExactly("Past Event", "Present Event", "Future Event 1", "Future Event 2", "Future Event 3");

        // Verify events are sorted by date in ascending order
        for (int i = 1; i < events.size(); i++) {
            assertThat(events.get(i - 1).getDateTime())
                    .isLessThanOrEqualTo(events.get(i).getDateTime());
        }
    }

    @Test
    public void shouldSortEventsDescendingByDateTime() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateTime"));

        // When
        Page<RunningEventEntity> result = repository.findAll(pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then
        assertThat(events).hasSize(5);
        assertThat(events)
                .extracting("name")
                .containsExactly("Future Event 3", "Future Event 2", "Future Event 1", "Present Event", "Past Event");

        // Verify events are sorted by date in descending order
        for (int i = 1; i < events.size(); i++) {
            assertThat(events.get(i - 1).getDateTime())
                    .isGreaterThanOrEqualTo(events.get(i).getDateTime());
        }
    }

    @Test
    public void shouldSortFilteredEventsAscendingByDateTime() {
        // Given
        Long fromDate = Instant.now().minus(5, ChronoUnit.DAYS).toEpochMilli();
        Long toDate = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dateTime"));

        // When
        Page<RunningEventEntity> result = repository.findByDateTimeBetween(fromDate, toDate, pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then
        assertThat(events).hasSize(2);
        assertThat(events).extracting("name").containsExactly("Present Event", "Future Event 1");

        // Verify events are sorted by date in ascending order and within filter range
        for (int i = 1; i < events.size(); i++) {
            assertThat(events.get(i - 1).getDateTime())
                    .isLessThanOrEqualTo(events.get(i).getDateTime());
            assertThat(events.get(i - 1).getDateTime()).isGreaterThanOrEqualTo(fromDate);
            assertThat(events.get(i - 1).getDateTime()).isLessThanOrEqualTo(toDate);
        }
    }

    @Test
    public void shouldSortFilteredEventsDescendingByDateTime() {
        // Given
        Long fromDate = Instant.now().minus(5, ChronoUnit.DAYS).toEpochMilli();
        Long toDate = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateTime"));

        // When
        Page<RunningEventEntity> result = repository.findByDateTimeBetween(fromDate, toDate, pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then
        assertThat(events).hasSize(2);
        assertThat(events).extracting("name").containsExactly("Future Event 1", "Present Event");

        // Verify events are sorted by date in descending order and within filter range
        for (int i = 1; i < events.size(); i++) {
            assertThat(events.get(i - 1).getDateTime())
                    .isGreaterThanOrEqualTo(events.get(i).getDateTime());
            assertThat(events.get(i - 1).getDateTime()).isGreaterThanOrEqualTo(fromDate);
            assertThat(events.get(i - 1).getDateTime()).isLessThanOrEqualTo(toDate);
        }
    }

    @Test
    public void shouldSortByNameAscending() {
        // Given - clear and setup with events that have different names
        repository.deleteAll();

        repository.save(RunningEventEntity.builder()
                .name("B Marathon")
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Location B")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("A Marathon")
                .dateTime(Instant.now().plus(40, ChronoUnit.DAYS).toEpochMilli())
                .location("Location A")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("C Marathon")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Location C")
                .build());

        // When - sort by name ascending
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<RunningEventEntity> result = repository.findAll(pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then - should be ordered by name alphabetically
        assertThat(events).hasSize(3);
        assertThat(events).extracting("name").containsExactly("A Marathon", "B Marathon", "C Marathon");
    }

    @Test
    public void shouldSortByNameDescending() {
        // Given - clear and setup with events that have different names
        repository.deleteAll();

        repository.save(RunningEventEntity.builder()
                .name("B Marathon")
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("Location B")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("A Marathon")
                .dateTime(Instant.now().plus(40, ChronoUnit.DAYS).toEpochMilli())
                .location("Location A")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("C Marathon")
                .dateTime(Instant.now().plus(20, ChronoUnit.DAYS).toEpochMilli())
                .location("Location C")
                .build());

        // When - sort by name descending
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        Page<RunningEventEntity> result = repository.findAll(pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then - should be ordered by name in reverse alphabetical order
        assertThat(events).hasSize(3);
        assertThat(events).extracting("name").containsExactly("C Marathon", "B Marathon", "A Marathon");
    }

    @Test
    public void shouldSortByLocationWithDateFilter() {
        // Given - clear and setup
        repository.deleteAll();

        Long present = Instant.now().toEpochMilli();
        Long future30Days = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();
        Long future60Days = Instant.now().plus(60, ChronoUnit.DAYS).toEpochMilli();

        // Events in filter range (between present and future30Days)
        repository.save(RunningEventEntity.builder()
                .name("Event B")
                .dateTime(Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli())
                .location("Location B")
                .build());

        repository.save(RunningEventEntity.builder()
                .name("Event A")
                .dateTime(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli())
                .location("Location A")
                .build());

        // Event outside filter range
        repository.save(RunningEventEntity.builder()
                .name("Event C")
                .dateTime(Instant.now().plus(45, ChronoUnit.DAYS).toEpochMilli())
                .location("Location C")
                .build());

        // When - find events in date range, sorted by location
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "location"));
        Page<RunningEventEntity> result = repository.findByDateTimeBetween(present, future30Days, pageRequest);
        List<RunningEventEntity> events = result.getContent();

        // Then - should only include events in the date range, sorted by location
        assertThat(events).hasSize(2);
        assertThat(events).extracting("name").containsExactly("Event A", "Event B");

        // Same test with descending order
        pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "location"));
        result = repository.findByDateTimeBetween(present, future30Days, pageRequest);
        events = result.getContent();

        assertThat(events).hasSize(2);
        assertThat(events).extracting("name").containsExactly("Event B", "Event A");
    }
}
