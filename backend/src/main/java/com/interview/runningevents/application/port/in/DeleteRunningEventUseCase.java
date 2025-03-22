package com.interview.runningevents.application.port.in;

/**
 * Input port for deleting a running event.
 * This use case allows clients to remove a running event from the system.
 */
public interface DeleteRunningEventUseCase {

    /**
     * Deletes a running event by its unique identifier.
     *
     * @param id The unique identifier of the running event to delete. Must not be null.
     * @return true if the event was successfully deleted, false if no event exists with the given ID.
     *
     * @throws IllegalArgumentException if the ID is null
     * @throws RuntimeException if there's an error during the deletion process
     */
    boolean deleteRunningEvent(Long id);
}
