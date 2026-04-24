package com.interview.autoshop.service;

import com.interview.autoshop.controller.dto.CreateAutoshopRequest;
import com.interview.autoshop.controller.dto.UpdateAutoshopRequest;
import com.interview.autoshop.repository.AutoshopEntity;
import com.interview.autoshop.repository.AutoshopRepository;
import com.interview.autoshop.service.domain.Autoshop;
import com.interview.autoshop.service.domain.AutoshopMapper;
import com.interview.error.exception.AutoshopNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD service for the Autoshop aggregate. */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutoshopService {

    private final AutoshopRepository repository;
    private final AutoshopMapper mapper;

    /**
     * Loads a single autoshop by id.
     *
     * @throws AutoshopNotFoundException if no such row exists
     */
    @Transactional(readOnly = true)
    public Autoshop findById(Long id) {
        log.info("Loading autoshop id={}", id);
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new AutoshopNotFoundException(id));
    }

    /** Returns a paginated list of autoshops. */
    @Transactional(readOnly = true)
    public Page<Autoshop> findAll(Pageable pageable) {
        log.info("Listing autoshops page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    /** Persists a new autoshop; id and timestamps are populated by the database. */
    public Autoshop create(CreateAutoshopRequest request) {
        log.info("Creating autoshop name='{}'", request.getName());
        Autoshop incoming = mapper.fromCreate(request);
        AutoshopEntity saved = repository.save(mapper.toEntity(incoming));
        return mapper.toDomain(saved);
    }

    /**
     * Replaces every mutable field of an existing autoshop; id and createdAt are preserved.
     *
     * @throws AutoshopNotFoundException if no such row exists
     */
    public Autoshop replace(Long id, UpdateAutoshopRequest request) {
        log.info("Replacing autoshop id={}", id);
        AutoshopEntity managed = repository.findById(id)
                .orElseThrow(() -> new AutoshopNotFoundException(id));
        mapper.applyUpdate(request, managed);
        return mapper.toDomain(repository.save(managed));
    }

    /**
     * Deletes an autoshop by id.
     *
     * @throws AutoshopNotFoundException if no such row exists
     */
    public void delete(Long id) {
        log.info("Deleting autoshop id={}", id);
        if (!repository.existsById(id)) {
            throw new AutoshopNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
