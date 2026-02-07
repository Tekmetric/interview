package com.interview.service.impl;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.dto.VehiclePageResponse;
import com.interview.model.dto.VehicleRequest;
import com.interview.model.dto.VehicleResponse;
import com.interview.model.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        log.info("Creating vehicle: {} {} {}", request.getMake(), request.getModel(), request.getYear());

        Vehicle vehicle = mapToEntity(request);
        Vehicle saved = vehicleRepository.save(vehicle);

        log.info("Vehicle created with id: {}", saved.getId());

        return VehicleResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        log.info("Fetching vehicle with id: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found with id: {}", id);
                    return new ResourceNotFoundException("Vehicle", "id", id);
                });

        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        log.info("Fetching all vehicles");

        return vehicleRepository.findAll()
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiclePageResponse getAllVehicles(Pageable pageable) {
        log.info("Fetching vehicles - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Vehicle> page = vehicleRepository.findAll(pageable);
        List<VehicleResponse> content = page.getContent()
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());

        return new VehiclePageResponse(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isLast()
        );
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        log.info("Updating vehicle with id: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found with id: {}", id);
                    return new ResourceNotFoundException("Vehicle", "id", id);
                });

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVin(request.getVin());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setOwnerPhone(request.getOwnerPhone());

        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle updated with id: {}", id);

        return VehicleResponse.fromEntity(updated);
    }

    @Override
    public void deleteVehicle(Long id) {
        log.info("Deleting vehicle with id: {}", id);

        if (!vehicleRepository.existsById(id)) {
            log.warn("Vehicle not found with id: {}", id);
            throw new ResourceNotFoundException("Vehicle", "id", id);
        }

        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted with id: {}", id);
    }

    private Vehicle mapToEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVin(request.getVin());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setOwnerPhone(request.getOwnerPhone());
        return vehicle;
    }
}