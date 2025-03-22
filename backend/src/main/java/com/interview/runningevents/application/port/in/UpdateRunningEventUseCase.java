package com.interview.runningevents.application.port.in;

import java.util.Optional;

import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Input port for updating an existing running event.
 * This use case allows clients to modify the details of a running event that already exists in the system.
 */
public interface UpdateRunningEventUseCase {

    /**
     * Updates an existing running event in the system.
     *
     * @param runningEvent The running event with updated information. Must not be null and must pass validation.
     *                     The id field must correspond to an existing event.
     * @return An Optional containing the updated running event if the update was successful,
     *         or empty if no event exists with the given ID.
     *
     * @throws IllegalArgumentException if:
     *         - The running event is null
     *         - The ID is null (cannot identify which event to update)
     *         - The event fails validation:
     *           - If required fields (name, dateTime, location) are missing
     *           - If string fields exceed maximum lengths
     * @throws RuntimeException if there's an error during the update process
     */
    Optional<RunningEvent> updateRunningEvent(RunningEvent runningEvent);
}
