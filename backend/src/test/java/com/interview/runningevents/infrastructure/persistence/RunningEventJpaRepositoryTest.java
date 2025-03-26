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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class RunningEventJpaRepositoryTest {

    @Autowired
    private RunningEventJpaRepository repository;

    @BeforeEach
    void setup() {
        // Clear all existing data
        repository.deleteAll();

        // Insert test data
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
    public void shouldSaveAndFindEntity() {
        // Given
        Long eventTime = Instant.now().plus(40, ChronoUnit.DAYS).toEpochMilli();
        RunningEventEntity newEvent = RunningEventEntity.builder()
                .name("New Test Event")
                .dateTime(eventTime)
                .location("Test Location")
                .description("Test Description")
                .build();

        // When
        RunningEventEntity savedEvent = repository.save(newEvent);
        RunningEventEntity foundEvent = repository.findById(savedEvent.getId()).orElse(null);

        // Then
        assertThat(savedEvent.getId()).isNotNull();
        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getName()).isEqualTo("New Test Event");
        assertThat(foundEvent.getDateTime()).isEqualTo(eventTime);
        assertThat(foundEvent.getLocation()).isEqualTo("Test Location");
        assertThat(foundEvent.getDescription()).isEqualTo("Test Description");
    }

    @Test
    public void shouldFilterByDateRange() {
        // Given
        Long startDate = Instant.now().minus(5, ChronoUnit.DAYS).toEpochMilli();
        Long endDate = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();

        // When
        Page<RunningEventEntity> result = repository.findByDateTimeBetween(startDate, endDate, PageRequest.of(0, 10));

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        List<RunningEventEntity> events = result.getContent();
        // Verify ordering by dateTime
        assertThat(events).extracting("dateTime").isSorted();
        assertThat(events).extracting("name").containsExactly("Present Event", "Future Event 1");
    }

    @Test
    public void shouldReturnEventsOrderedByDateTime() {
        // When
        List<RunningEventEntity> events =
                repository.findAll(PageRequest.of(0, 10)).getContent();

        // Then
        assertThat(events).extracting("dateTime").isSorted();
        assertThat(events)
                .extracting("name")
                .containsExactly("Past Event", "Present Event", "Future Event 1", "Future Event 2", "Future Event 3");
    }

    @Test
    public void shouldPaginateResults() {
        // Given
        PageRequest firstPage = PageRequest.of(0, 2);
        PageRequest secondPage = PageRequest.of(1, 2);

        // When
        Page<RunningEventEntity> firstPageResult = repository.findAll(firstPage);
        Page<RunningEventEntity> secondPageResult = repository.findAll(secondPage);

        // Then
        assertThat(firstPageResult.getTotalElements()).isEqualTo(5);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(3);
        assertThat(firstPageResult.getNumber()).isEqualTo(0);
        assertThat(firstPageResult.getSize()).isEqualTo(2);
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getContent()).extracting("name").containsExactly("Past Event", "Present Event");

        assertThat(secondPageResult.getTotalElements()).isEqualTo(5);
        assertThat(secondPageResult.getNumber()).isEqualTo(1);
        assertThat(secondPageResult.getContent()).hasSize(2);
        assertThat(secondPageResult.getContent())
                .extracting("name")
                .containsExactly("Future Event 1", "Future Event 2");
    }
}
