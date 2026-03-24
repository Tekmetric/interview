package com.interview.service;

import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.repository.VehicleSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findAll(VehicleSearchCriteria criteria, Pageable pageable) {
        return vehicleRepository.findAll(VehicleSpecification.fromCriteria(criteria), pageable);
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    // Use REQUIRES_NEW to force hibernate to flush, otherwise state like createdAt will be null when this participates in existing transactions
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Vehicle create(Vehicle vehicle) {
        Vehicle createdVehicle = vehicleRepository.save(vehicle);
        log.info("Created vehicle id={} vin={}", createdVehicle.getId(), createdVehicle.getVin());
        return createdVehicle;
    }

    @Transactional
    public Vehicle update(Long id, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        existingVehicle.setModelYear(vehicle.getModelYear());
        existingVehicle.setMake(vehicle.getMake());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setColor(vehicle.getColor());
        existingVehicle.setLicensePlate(vehicle.getLicensePlate());
        existingVehicle.setVin(vehicle.getVin());
        existingVehicle.setFuelType(vehicle.getFuelType());
        existingVehicle.setDoors(vehicle.getDoors());
        existingVehicle.setMileage(vehicle.getMileage());

        log.info("Updated vehicle id={} vin={}", existingVehicle.getId(), existingVehicle.getVin());
        return existingVehicle;
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        vehicleRepository.deleteById(id);
        log.info("Deleted vehicle id={}", id);
    }
}
