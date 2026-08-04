package com.interview.service;

import com.interview.dto.VehicleFilter;
import com.interview.dto.VehiclePatchRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import com.interview.exception.VehicleNotFoundException;
import com.interview.repository.VehicleRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle saved = repository.save(VehicleMapper.toEntity(request));
        return VehicleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(long id) {
        return repository
                .findById(id)
                .map(VehicleMapper::toResponse)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponse> search(VehicleFilter filter, Pageable pageable) {
        return repository.findAll(toSpecification(filter), pageable).map(VehicleMapper::toResponse);
    }

    @Transactional
    public VehicleResponse update(long id, VehiclePatchRequest request) {
        Vehicle vehicle = repository.findById(id).orElseThrow(() -> new VehicleNotFoundException(id));
        VehicleMapper.applyPatch(request, vehicle);
        return VehicleMapper.toResponse(repository.save(vehicle));
    }

    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }

    private static Specification<Vehicle> toSpecification(VehicleFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.make() != null && !filter.make().isBlank()) {
                predicates.add(cb.equal(root.get("makeLower"), filter.make().toLowerCase()));
            }
            if (filter.model() != null && !filter.model().isBlank()) {
                predicates.add(cb.equal(root.get("modelLower"), filter.model().toLowerCase()));
            }
            if (filter.year() != null) {
                predicates.add(cb.equal(root.get("year"), filter.year()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
