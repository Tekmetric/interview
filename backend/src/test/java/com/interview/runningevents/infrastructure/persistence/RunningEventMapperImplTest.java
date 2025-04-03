package com.interview.runningevents.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.interview.runningevents.domain.model.RunningEvent;

public class RunningEventMapperImplTest {

    private RunningEventMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new RunningEventMapperImpl();
    }

    @Test
    void shouldMapDomainToEntity() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEvent domainEvent = RunningEvent.builder()
                .id(1L)
                .name("Test Event")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        RunningEventEntity entity = mapper.toEntity(domainEvent);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Test Event");
        assertThat(entity.getDateTime()).isEqualTo(futureTime);
        assertThat(entity.getLocation()).isEqualTo("Test Location");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    void shouldMapEntityToDomain() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventEntity entity = RunningEventEntity.builder()
                .id(1L)
                .name("Test Event")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        RunningEvent domainEvent = mapper.toDomain(entity);

        // Then
        assertThat(domainEvent).isNotNull();
        assertThat(domainEvent.getId()).isEqualTo(1L);
        assertThat(domainEvent.getName()).isEqualTo("Test Event");
        assertThat(domainEvent.getDateTime()).isEqualTo(futureTime);
        assertThat(domainEvent.getLocation()).isEqualTo("Test Location");
        assertThat(domainEvent.getDescription()).isEqualTo("Test Description");
        assertThat(domainEvent.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    void shouldHandleNullValues() {
        // When
        RunningEventEntity entity = mapper.toEntity(null);
        RunningEvent domain = mapper.toDomain(null);

        // Then
        assertThat(entity).isNull();
        assertThat(domain).isNull();
    }

    @Test
    void shouldHandlePartialData() {
        // Given
        RunningEvent partialDomain = RunningEvent.builder()
                .id(1L)
                .name("Partial Event")
                .dateTime(Instant.now().toEpochMilli())
                .location("Test Location")
                .build();

        RunningEventEntity partialEntity = RunningEventEntity.builder()
                .id(2L)
                .name("Partial Entity")
                .dateTime(Instant.now().toEpochMilli())
                .location("Entity Location")
                .build();

        // When
        RunningEventEntity mappedEntity = mapper.toEntity(partialDomain);
        RunningEvent mappedDomain = mapper.toDomain(partialEntity);

        // Then
        assertThat(mappedEntity).isNotNull();
        assertThat(mappedEntity.getId()).isEqualTo(1L);
        assertThat(mappedEntity.getName()).isEqualTo("Partial Event");
        assertThat(mappedEntity.getDescription()).isNull();
        assertThat(mappedEntity.getFurtherInformation()).isNull();

        assertThat(mappedDomain).isNotNull();
        assertThat(mappedDomain.getId()).isEqualTo(2L);
        assertThat(mappedDomain.getName()).isEqualTo("Partial Entity");
        assertThat(mappedDomain.getDescription()).isNull();
        assertThat(mappedDomain.getFurtherInformation()).isNull();
    }
}
