package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.exception.DuplicateVinException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository repo;

    @Transactional
    public Vehicle create(VehicleRequest req) {
        Vehicle v = new Vehicle();
        v.setVin(req.getVin());
        v.setMake(req.getMake());
        v.setModel(req.getModel());
        v.setYear(req.getYear());
        return repo.save(v);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Vehicle getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found, id: " + id));
    }

    @Transactional
    public Vehicle update(Long id, VehicleRequest req) {
        Vehicle existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found, id: " + id));

        // If VIN is changing, ensure it's not used by another vehicle
        if (!existing.getVin().equals(req.getVin())) {
            if (repo.existsByVinAndIdNot(req.getVin(), id)) {
                throw new DuplicateVinException("VIN already exists");
            }
        }

        existing.setVin(req.getVin());
        existing.setMake(req.getMake());
        existing.setModel(req.getModel());
        existing.setYear(req.getYear());

        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle v = getById(id);
        repo.delete(v);
    }
}
