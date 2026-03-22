package com.interview.mapper;

import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>Type parameters:
 * <ul>
 *   <li>{@code E} — JPA entity</li>
 *   <li>{@code R} — response DTO</li>
 *   <li>{@code C} — create request DTO</li>
 *   <li>{@code U} — update request DTO</li>
 * </ul>
 *
 */
public interface EntityMapper<E, R, C, U> {

    R toResponse(E entity);

    E toEntity(C createRequest);

    void updateEntity(U updateRequest, @MappingTarget E entity);

    List<R> toResponseList(List<E> entities);

    default ZonedDateTime toZonedDateTime(final Instant instant) {
        return instant == null ? null : instant.atZone(ZoneOffset.UTC);
    }
}
