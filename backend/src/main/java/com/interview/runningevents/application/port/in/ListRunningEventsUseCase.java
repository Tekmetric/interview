package com.interview.runningevents.application.port.in;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Input port for listing and filtering running events.
 * This use case allows clients to retrieve a paginated list of running events with optional filtering.
 */
public interface ListRunningEventsUseCase {

    /**
     * Lists running events based on the provided query parameters.
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
     *
     * @throws IllegalArgumentException if the query parameters are invalid:
     *         - If page is negative
     *         - If pageSize is negative or zero
     *         - If fromDate is after toDate
     * @throws RuntimeException if there's an error during the retrieval process
     */
    PaginatedResult<RunningEvent> listRunningEvents(RunningEventQuery query);
}
