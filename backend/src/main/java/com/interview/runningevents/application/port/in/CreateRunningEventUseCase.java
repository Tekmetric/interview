package com.interview.runningevents.application.port.in;

import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Input port for creating a new running event.
 * This use case allows clients to create a new running event in the system.
 */
public interface CreateRunningEventUseCase {

    /**
     * Creates a new running event in the system.
     *
     * @param runningEvent The running event to create. Must not be null and must pass validation.
     *                     The id field should be null as it will be assigned by the system.
     * @return The created running event with the system-assigned ID.
     *
     * @throws IllegalArgumentException if the running event fails validation:
     *         - If required fields (name, dateTime, location) are missing
     *         - If string fields exceed maximum lengths
     * @throws RuntimeException if there's an error during the creation process
     */
    RunningEvent createRunningEvent(RunningEvent runningEvent);
}
