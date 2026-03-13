package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.exception.ConcurrentUpdateException;
import com.interview.exception.DuplicateVinException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository repo;

    @Transactional
    public Vehicle create(VehicleRequest req) {
        log.info("Creating vehicle with VIN={}", req.getVin());

        Vehicle v = new Vehicle();
        v.setVin(req.getVin());
        v.setMake(req.getMake());
        v.setModel(req.getModel());
        v.setYear(req.getYear());

        Vehicle saved = repo.save(v);

        log.info("Created vehicle id={} VIN={}", saved.getId(), saved.getVin());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> getAll(Pageable pageable) {
        log.info("Fetching vehicles page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Vehicle> page = repo.findAll(pageable);
        log.info("Fetched {} vehicles", page.getNumberOfElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Vehicle getById(Long id) {
        log.info("Fetching vehicle id={}", id);

        return repo.findById(id)
                .map(v -> {
                    log.info("Found vehicle id={} VIN={}", v.getId(), v.getVin());
                    return v;
                })
                .orElseThrow(() -> {
                    log.warn("Vehicle not found id={}", id);
                    return new ResourceNotFoundException("Vehicle not found, id: " + id);
                });
    }

    @Transactional
    public Vehicle update(Long id, VehicleRequest req) {
        log.info("Updating vehicle id={} with payload={}", id, req);

        Vehicle existing = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found id={} for update", id);
                    return new ResourceNotFoundException("Vehicle not found, id: " + id);
                });

        if (!existing.getVin().equals(req.getVin())) {
            if (repo.existsByVinAndIdNot(req.getVin(), id)) {
                log.warn("Duplicate VIN={} attempted for vehicle id={}", req.getVin(), id);
                throw new DuplicateVinException("VIN already exists");
            }
        }

        existing.setVin(req.getVin());
        existing.setMake(req.getMake());
        existing.setModel(req.getModel());
        existing.setYear(req.getYear());

        try {
            Vehicle saved = repo.save(existing);
            log.info("Updated vehicle id={} newVersion={}", saved.getId(), saved.getVersion());
            return saved;
        } catch (OptimisticLockException ex) {
            log.warn("Optimistic lock conflict updating vehicle id={}", id);
            throw new ConcurrentUpdateException("Vehicle was updated by another request");
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting vehicle id={}", id);

        Vehicle v = getById(id);
        repo.delete(v);

        log.info("Deleted vehicle id={}", id);
    }
}
