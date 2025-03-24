package com.interview.runningevents.infrastructure.persistence;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Implementation of the RunningEventRepository port that adapts to the
 * Spring Data JPA repository.
 */
@Component
public class RunningEventRepositoryAdapter implements RunningEventRepository {

    private final RunningEventJpaRepository jpaRepository;

    public RunningEventRepositoryAdapter(RunningEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RunningEvent save(RunningEvent runningEvent) {
        if (runningEvent == null) {
            throw new IllegalArgumentException("Running event cannot be null");
        }

        RunningEventEntity entity = RunningEventMapper.toEntity(runningEvent);
        RunningEventEntity savedEntity = jpaRepository.save(entity);
        return RunningEventMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RunningEvent> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return jpaRepository.findById(id).map(RunningEventMapper::toDomain);
    }

    @Override
    public PaginatedResult<RunningEvent> findAll(RunningEventQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        // Create pageable request
        PageRequest pageRequest = PageRequest.of(query.getPage(), query.getPageSize());

        Page<RunningEventEntity> page;

        // Execute query with date range filter if specified
        if (query.getFromDate() != null && query.getToDate() != null) {
            page = jpaRepository.findByDateTimeBetweenOrderByDateTime(
                    query.getFromDate(), query.getToDate(), pageRequest);
        } else {
            page = jpaRepository.findAllByOrderByDateTime(pageRequest);
        }

        // Map entities to domain objects
        return new PaginatedResult<>(
                page.getContent().stream().map(RunningEventMapper::toDomain).collect(Collectors.toList()),
                page.getTotalElements(),
                query.getPage(),
                query.getPageSize(),
                page.getTotalPages(),
                page.hasPrevious(),
                page.hasNext());
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return jpaRepository.existsById(id);
    }
}
