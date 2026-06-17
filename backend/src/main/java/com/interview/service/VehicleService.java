package com.interview.service;

import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.AppUser;
import com.interview.entity.Vehicle;
import com.interview.repository.AppUserRepository;
import com.interview.repository.VehicleRepository;
import com.interview.repository.VehicleSpecification;
import com.interview.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final AppUserRepository appUserRepository;

    public Page<Vehicle> findAll(VehicleSearchCriteria criteria, Pageable pageable, AuthenticatedUser currentUser) {
        return vehicleRepository.findAll(
                VehicleSpecification.fromCriteria(criteria).and(ownerScope(currentUser)),
                pageable
        );
    }

    public Vehicle findById(Long id, AuthenticatedUser currentUser) {
        return findAccessibleVehicle(id, currentUser);
    }

    @Transactional
    public Vehicle create(Vehicle vehicle, AuthenticatedUser currentUser) {
        AppUser owner = appUserRepository.getReferenceById(currentUser.id());
        vehicle.setOwner(owner);

        Vehicle createdVehicle = vehicleRepository.saveAndFlush(vehicle);
        log.info("Created vehicle id={} vin={} ownerId={}", createdVehicle.getId(), createdVehicle.getVin(), currentUser.id());
        return createdVehicle;
    }

    @Transactional
    public Vehicle update(Long id, Vehicle vehicle, AuthenticatedUser currentUser) {
        Vehicle existingVehicle = findAccessibleVehicle(id, currentUser);

        existingVehicle.setModelYear(vehicle.getModelYear());
        existingVehicle.setMake(vehicle.getMake());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setColor(vehicle.getColor());
        existingVehicle.setLicensePlate(vehicle.getLicensePlate());
        existingVehicle.setVin(vehicle.getVin());
        existingVehicle.setFuelType(vehicle.getFuelType());
        existingVehicle.setDoors(vehicle.getDoors());
        existingVehicle.setMileage(vehicle.getMileage());

        log.info("Updated vehicle id={} vin={} ownerId={}", existingVehicle.getId(), existingVehicle.getVin(), existingVehicle.getOwner().getId());
        return existingVehicle;
    }

    @Transactional
    public void delete(Long id, AuthenticatedUser currentUser) {
        Vehicle vehicle = findAccessibleVehicle(id, currentUser);
        vehicleRepository.delete(vehicle);
        log.info("Deleted vehicle id={} ownerId={}", id, vehicle.getOwner().getId());
    }

    private Vehicle findAccessibleVehicle(Long id, AuthenticatedUser currentUser) {
        if (currentUser.isAdmin()) {
            return vehicleRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new);
        }

        return vehicleRepository.findByIdAndOwnerId(id, currentUser.id())
                .orElseThrow(EntityNotFoundException::new);
    }

    private static Specification<Vehicle> ownerScope(AuthenticatedUser currentUser) {
        return currentUser.isAdmin()
                ? Specification.unrestricted()
                : VehicleSpecification.ownedBy(currentUser.id());
    }
}
