package com.interview.service;

import com.interview.dto.PaginatedResponse;
import com.interview.dto.VehicleFilterRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.mapper.VehicleMapper;
import com.interview.model.Role;
import com.interview.model.User;
import com.interview.model.Vehicle;
import com.interview.repository.UserRepository;
import com.interview.repository.VehicleRepository;
import com.interview.repository.specification.VehicleSpecification;
import com.interview.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;
    private final SecurityService securityService;

    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        User currentUser = securityService.getCurrentUser();

        if (!currentUser.getId().equals(request.getOwnerId())) {
            throw new RuntimeException("You can only create vehicles for yourself");
        }

        Vehicle vehicle = Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .registrationYear(request.getRegistrationYear())
                .licensePlate(request.getLicensePlate())
                .owner(currentUser)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<VehicleResponse> getAllVehicles(VehicleFilterRequest filter, Pageable pageable) {
        Specification<Vehicle> spec = VehicleSpecification.withFilters(filter);
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);
        
        List<VehicleResponse> content = vehicleMapper.toResponseList(vehiclePage.getContent());
        
        return PaginatedResponse.<VehicleResponse>builder()
                .content(content)
                .page(vehiclePage.getNumber())
                .size(vehiclePage.getSize())
                .totalElements(vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        User currentUser = securityService.getCurrentUser();

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Check if the user is the owner OR is an admin
        boolean isOwner = vehicle.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        return vehicleMapper.toResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByOwner(Long ownerId) {
        User currentUser = securityService.getCurrentUser();

        boolean isOwner = ownerId.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return vehicleMapper.toResponseList(vehicleRepository.findByOwner(owner));
    }

    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        User currentUser = securityService.getCurrentUser();

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        boolean isOwner = request.getOwnerId().equals(currentUser.getId());
        if (!isOwner) {
            throw new RuntimeException("Access denied");
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationYear(request.getRegistrationYear());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setOwner(owner);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        User currentUser = securityService.getCurrentUser();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        boolean isOwner = vehicle.getOwner().getId().equals(currentUser.getId());
        if (!isOwner) {
            throw new RuntimeException("Access denied");
        }

        if (vehicle.getDeletedAt() != null) {
            throw new RuntimeException("Vehicle already deleted");
        }
        
        vehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
    }
}