package com.interview.runningevents.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Service implementation for running event use cases.
 * Implements the use case interfaces and coordinates the business logic
 * for creating, retrieving, and listing running events.
 */
@Service
public class RunningEventService
        implements CreateRunningEventUseCase, GetRunningEventUseCase, ListRunningEventsUseCase {

    private final RunningEventRepository runningEventRepository;

    /**
     * Creates a new RunningEventService with the necessary dependencies.
     *
     * @param runningEventRepository The repository for persisting and retrieving running events
     */
    public RunningEventService(RunningEventRepository runningEventRepository) {
        this.runningEventRepository = runningEventRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RunningEvent createRunningEvent(RunningEvent runningEvent) {
        validateRunningEvent(runningEvent);

        // Ensure the ID is null for new entities
        if (runningEvent.getId() != null) {
            throw new ValidationException("ID must be null when creating a new running event");
        }

        return runningEventRepository.save(runningEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RunningEvent> getRunningEventById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return runningEventRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<RunningEvent> listRunningEvents(RunningEventQuery query) {
        validateQuery(query);
        return runningEventRepository.findAll(query);
    }

    /**
     * Validates a running event query parameters.
     *
     * @param query The query to validate
     * @throws IllegalArgumentException if the query parameters are invalid
     */
    private void validateQuery(RunningEventQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        if (query.getPage() != null && query.getPage() < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (query.getPageSize() != null && query.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }

        if (query.getFromDate() != null && query.getToDate() != null && query.getFromDate() > query.getToDate()) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
    }

    /**
     * Validates a running event according to business rules.
     *
     * @param runningEvent The running event to validate
     * @throws ValidationException if the running event fails validation
     */
    private void validateRunningEvent(RunningEvent runningEvent) {
        if (runningEvent == null) {
            throw new ValidationException("Running event cannot be null");
        }

        if (!runningEvent.isValid()) {
            throw new ValidationException(
                    "Invalid running event: check required fields, field lengths, and ensure the event date is in the future");
        }
    }
}
