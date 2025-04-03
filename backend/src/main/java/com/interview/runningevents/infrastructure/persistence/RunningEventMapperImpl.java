package com.interview.runningevents.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Mapper implementation for converting between RunningEvent domain model and RunningEventEntity JPA entity.
 */
@Component
public class RunningEventMapperImpl {

    /**
     * Maps a domain model RunningEvent to a JPA entity RunningEventEntity.
     *
     * @param runningEvent The domain model object to map
     * @return The mapped JPA entity
     */
    public RunningEventEntity toEntity(RunningEvent runningEvent) {
        if (runningEvent == null) {
            return null;
        }

        return RunningEventEntity.builder()
                .id(runningEvent.getId())
                .name(runningEvent.getName())
                .dateTime(runningEvent.getDateTime())
                .location(runningEvent.getLocation())
                .description(runningEvent.getDescription())
                .furtherInformation(runningEvent.getFurtherInformation())
                .build();
    }

    /**
     * Maps a JPA entity RunningEventEntity to a domain model RunningEvent.
     *
     * @param entity The JPA entity to map
     * @return The mapped domain model object
     */
    public RunningEvent toDomain(RunningEventEntity entity) {
        if (entity == null) {
            return null;
        }

        return RunningEvent.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dateTime(entity.getDateTime())
                .location(entity.getLocation())
                .description(entity.getDescription())
                .furtherInformation(entity.getFurtherInformation())
                .build();
    }
}
