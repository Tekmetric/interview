package com.interview.runningevents.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class RunningEventEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RunningEventJpaRepository repository;

    @Test
    public void shouldCreateAndRetrieveRunningEvent() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity = RunningEventEntity.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        RunningEventEntity savedEntity = entityManager.persistAndFlush(entity);

        // Then
        assertThat(savedEntity.getId()).isNotNull();

        Optional<RunningEventEntity> retrieved = repository.findById(savedEntity.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Test Marathon");
        assertThat(retrieved.get().getDateTime()).isEqualTo(futureTime);
        assertThat(retrieved.get().getLocation()).isEqualTo("Test Location");
        assertThat(retrieved.get().getDescription()).isEqualTo("Test Description");
        assertThat(retrieved.get().getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    public void shouldUpdateRunningEvent() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity = RunningEventEntity.builder()
                .name("Original Name")
                .dateTime(futureTime)
                .location("Original Location")
                .build();

        RunningEventEntity savedEntity = entityManager.persistAndFlush(entity);

        // When
        savedEntity.setName("Updated Name");
        savedEntity.setLocation("Updated Location");
        entityManager.persistAndFlush(savedEntity);

        // Then
        Optional<RunningEventEntity> retrieved = repository.findById(savedEntity.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated Name");
        assertThat(retrieved.get().getLocation()).isEqualTo("Updated Location");
    }

    @Test
    public void shouldDeleteRunningEvent() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity = RunningEventEntity.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        RunningEventEntity savedEntity = entityManager.persistAndFlush(entity);
        Long id = savedEntity.getId();

        // When
        repository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<RunningEventEntity> retrieved = repository.findById(id);
        assertThat(retrieved).isEmpty();
    }

    @Test
    public void shouldFindRunningEventsByDateRange() {
        // First clear all existing data
        repository.deleteAll();
        entityManager.flush();

        // Given - create test events with specific timestamps
        Long past = Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli();
        Long present = Instant.now().toEpochMilli();
        Long future = Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli();
        Long farFuture = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity pastEvent = RunningEventEntity.builder()
                .name("Past Event")
                .dateTime(past)
                .location("Past Location")
                .build();

        RunningEventEntity presentEvent = RunningEventEntity.builder()
                .name("Present Event")
                .dateTime(present)
                .location("Present Location")
                .build();

        RunningEventEntity futureEvent = RunningEventEntity.builder()
                .name("Future Event")
                .dateTime(future)
                .location("Future Location")
                .build();

        RunningEventEntity farFutureEvent = RunningEventEntity.builder()
                .name("Far Future Event")
                .dateTime(farFuture)
                .location("Far Future Location")
                .build();

        entityManager.persist(pastEvent);
        entityManager.persist(presentEvent);
        entityManager.persist(futureEvent);
        entityManager.persist(farFutureEvent);
        entityManager.flush();

        // When & Then
        // Find events from now to far future
        List<RunningEventEntity> futureEvents = repository
                .findByDateTimeBetween(present, farFuture, org.springframework.data.domain.Pageable.unpaged())
                .getContent();
        assertThat(futureEvents).hasSize(3);
        // Verify ordering is by dateTime ascending
        assertThat(futureEvents).extracting("dateTime").isSorted();
        assertThat(futureEvents)
                .extracting("name")
                .containsExactly("Present Event", "Future Event", "Far Future Event");

        // Find events in a specific range
        List<RunningEventEntity> rangeEvents = repository
                .findByDateTimeBetween(present, future, org.springframework.data.domain.Pageable.unpaged())
                .getContent();
        assertThat(rangeEvents).hasSize(2);
        // Verify ordering is by dateTime ascending
        assertThat(rangeEvents).extracting("dateTime").isSorted();
        assertThat(rangeEvents).extracting("name").containsExactly("Present Event", "Future Event");

        // Find all events ordered by date time
        List<RunningEventEntity> allEvents = repository
                .findAll(org.springframework.data.domain.Pageable.unpaged())
                .getContent();
        assertThat(allEvents).hasSize(4);
        // Verify ordering is by dateTime ascending
        assertThat(allEvents).extracting("dateTime").isSorted();
        assertThat(allEvents)
                .extracting("name")
                .containsExactly("Past Event", "Present Event", "Future Event", "Far Future Event");
    }
}
