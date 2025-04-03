package com.interview.runningevents.application.port.out;

import java.util.Optional;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Repository interface for RunningEvent domain entities.
 * This interface defines the contract for persistence operations on running events.
 * It acts as an output port in the hexagonal architecture, connecting the application
 * layer to the infrastructure layer responsible for persistence.
 */
public interface RunningEventRepository {

    /**
     * Saves a running event to the repository. This method handles both create and update operations.
     * For new entities (id is null), a new record will be created.
     * For existing entities (id is not null), the record will be updated if it exists.
     *
     * @param runningEvent The running event to save. Must not be null.
     * @return The saved running event with its ID (system-generated for new entities).
     * @throws IllegalArgumentException if the running event is null or fails validation
     * @throws RuntimeException if there's a persistence error
     */
    RunningEvent save(RunningEvent runningEvent);

    /**
     * Finds a running event by its unique identifier.
     *
     * @param id The unique identifier of the running event to find. Must not be null.
     * @return An Optional containing the running event if found, or empty if no event exists with the given ID.
     * @throws IllegalArgumentException if the ID is null
     * @throws RuntimeException if there's a persistence error
     */
    Optional<RunningEvent> findById(Long id);

    /**
     * Finds running events matching the specified query criteria with pagination.
     *
     * @param query The query parameters for filtering and pagination:
     *        - fromDate: Optional minimum date to filter events (inclusive)
     *        - toDate: Optional maximum date to filter events (inclusive)
     *        - page: The page number (0-based, defaults to 0)
     *        - pageSize: The number of items per page (defaults to 20)
     *        - sortBy: Field to sort by (defaults to "dateTime")
     *        - sortDirection: Sort direction ("ASC" or "DESC", defaults to "ASC")
     * @return A paginated result containing the matching running events and pagination metadata.
     *         Returns an empty result if no events match the criteria.
     * @throws IllegalArgumentException if the query parameters are invalid
     * @throws RuntimeException if there's a persistence error
     */
    PaginatedResult<RunningEvent> findAll(RunningEventQuery query);

    /**
     * Deletes a running event by its unique identifier.
     *
     * @param id The unique identifier of the running event to delete. Must not be null.
     * @return true if the running event was successfully deleted, false if no event exists with the given ID.
     * @throws IllegalArgumentException if the ID is null
     * @throws RuntimeException if there's a persistence error
     */
    boolean deleteById(Long id);

    /**
     * Checks if a running event exists with the specified ID.
     *
     * @param id The unique identifier to check. Must not be null.
     * @return true if a running event exists with the given ID, false otherwise.
     * @throws IllegalArgumentException if the ID is null
     * @throws RuntimeException if there's a persistence error
     */
    boolean existsById(Long id);
}
