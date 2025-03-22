package com.interview.runningevents.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Service implementation for running event use cases.
 * Implements the use case interfaces and coordinates the business logic
 * for creating and retrieving running events.
 */
@Service
public class RunningEventService implements CreateRunningEventUseCase, GetRunningEventUseCase {

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
            throw new ValidationException("Invalid running event: check required fields, field lengths");
        }
    }
}
