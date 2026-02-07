package com.interview.service.impl;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.dto.VehiclePageResponse;
import com.interview.model.dto.VehicleRequest;
import com.interview.model.dto.VehicleResponse;
import com.interview.model.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = mapToEntity(request);
        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiclePageResponse getAllVehicles(Pageable pageable) {
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
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

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
        return VehicleResponse.fromEntity(updated);
    }

    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", "id", id);
        }
        vehicleRepository.deleteById(id);
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