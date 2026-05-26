package com.interview.service;

import com.interview.dto.PagedResponse;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import com.interview.exception.ResourceConflictException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.VehicleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PagedResponse<VehicleResponse> findAll(int page, int size) {
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        return PagedResponse.of(vehiclePage.map(this::toResponse));
    }

    public VehicleResponse findById(Long id) {
        return toResponse(findVehicle(id));
    }

    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = toEntity(request);
        return saveVehicle(vehicle);
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest updates) {
        Vehicle existing = findVehicle(id);
        existing.setMake(updates.getMake());
        existing.setModel(updates.getModel());
        existing.setYear(updates.getYear());
        existing.setVin(updates.getVin());
        existing.setLicensePlate(updates.getLicensePlate());
        existing.setMileage(updates.getMileage());
        return saveVehicle(existing);
    }

    public void delete(Long id) {
        vehicleRepository.delete(findVehicle(id));
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    private VehicleResponse saveVehicle(Vehicle vehicle) {
        try {
            return toResponse(vehicleRepository.saveAndFlush(vehicle));
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceConflictException("Vehicle violates a uniqueness constraint", exception);
        }
    }

    private Vehicle toEntity(VehicleRequest request) {
        return new Vehicle(
                request.getMake(),
                request.getModel(),
                request.getYear(),
                request.getVin(),
                request.getLicensePlate(),
                request.getMileage()
        );
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getVin(),
                vehicle.getLicensePlate(),
                vehicle.getMileage()
        );
    }
}
