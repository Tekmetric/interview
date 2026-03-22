package com.interview.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Generic CRUD service contract.
 *
 * <p>Type parameters:
 * <ul>
 *   <li>{@code R} — response DTO returned to the caller</li>
 *   <li>{@code C} — create request DTO</li>
 *   <li>{@code U} — update request DTO</li>
 * </ul>
 *
 */
public interface CrudService<R, C, U> {

    R create(final C request);

    R findById(final UUID id);

    Page<R> findAll(final Pageable pageable);

    R update(final UUID id, final U request);

    void delete(final UUID id);
}
