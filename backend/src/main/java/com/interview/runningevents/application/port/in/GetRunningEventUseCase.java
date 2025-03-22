package com.interview.runningevents.application.port.in;

import java.util.Optional;

import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Input port for retrieving a single running event by its ID.
 * This use case allows clients to fetch the details of a specific running event.
 */
public interface GetRunningEventUseCase {

    /**
     * Retrieves a running event by its unique identifier.
     *
     * @param id The unique identifier of the running event to retrieve. Must not be null.
     * @return An Optional containing the running event if found, or empty if no event exists with the given ID.
     *
     * @throws IllegalArgumentException if the ID is null
     * @throws RuntimeException if there's an error during the retrieval process
     */
    Optional<RunningEvent> getRunningEventById(Long id);
}
