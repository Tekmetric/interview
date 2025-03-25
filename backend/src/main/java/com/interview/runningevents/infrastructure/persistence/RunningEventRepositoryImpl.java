package com.interview.runningevents.infrastructure.persistence;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.application.port.out.RunningEventRepository;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Implementation of the RunningEventRepository port that uses JPA for persistence.
 */
@Component
public class RunningEventRepositoryImpl implements RunningEventRepository {

    private final RunningEventJpaRepository jpaRepository;
    private final RunningEventMapperImpl mapper;

    public RunningEventRepositoryImpl(RunningEventJpaRepository jpaRepository, RunningEventMapperImpl mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RunningEvent save(RunningEvent runningEvent) {
        if (runningEvent == null) {
            throw new IllegalArgumentException("Running event cannot be null");
        }

        RunningEventEntity entity = mapper.toEntity(runningEvent);
        RunningEventEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RunningEvent> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PaginatedResult<RunningEvent> findAll(RunningEventQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        // Create pageable request with sort direction
        org.springframework.data.domain.Sort.Direction direction = query.getSortDirection() == SortDirection.DESC
                ? org.springframework.data.domain.Sort.Direction.DESC
                : org.springframework.data.domain.Sort.Direction.ASC;

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(direction, "dateTime");

        PageRequest pageRequest = PageRequest.of(query.getPage(), query.getPageSize(), sort);

        Page<RunningEventEntity> page;

        // Execute query with date range filter if specified
        if (query.getFromDate() != null && query.getToDate() != null) {
            page = jpaRepository.findByDateTimeBetween(query.getFromDate(), query.getToDate(), pageRequest);
        } else {
            page = jpaRepository.findAll(pageRequest);
        }

        // Map entities to domain objects
        return new PaginatedResult<>(
                page.getContent().stream().map(mapper::toDomain).collect(Collectors.toList()),
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
