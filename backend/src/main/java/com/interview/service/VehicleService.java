package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse findById(UUID id) {
        return vehicleRepository.findById(id)
                .map(VehicleService::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Page<VehicleResponse> findAll(String make, Integer year, Pageable pageable) {
        return vehicleRepository.findAll(make, year, pageable)
                .map(VehicleService::toResponse);
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVin(request.vin())) {
            throw new ResourceAlreadyExistsException("Vehicle", "vin", request.vin());
        }

        Vehicle entity = new Vehicle();
        entity.setId(UUID.randomUUID());
        applyRequest(entity, request);
        return toResponse(vehicleRepository.save(entity));
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (!existing.getVin().equals(request.vin()) &&
                vehicleRepository.existsByVin(request.vin())) {
            throw new ResourceAlreadyExistsException("Vehicle", "vin", request.vin());
        }

        applyRequest(existing, request);
        return toResponse(vehicleRepository.save(existing));
    }

    @Transactional
    public void deleteById(UUID id) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        vehicleRepository.delete(existing);
    }

    private static void applyRequest(Vehicle entity, VehicleRequest request) {
        entity.setMake(request.make());
        entity.setModel(request.model());
        entity.setYear(request.year());
        entity.setVin(request.vin());
        entity.setMileage(request.mileage());
    }

    private static VehicleResponse toResponse(Vehicle entity) {
        return new VehicleResponse(
                entity.getId(),
                entity.getMake(),
                entity.getModel(),
                entity.getYear(),
                entity.getVin(),
                entity.getMileage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}