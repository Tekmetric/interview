package com.interview.resource.service;

import com.interview.resource.model.PaginatedResponse;
import com.interview.resource.model.PaginationMeta;
import com.interview.resource.model.Vehicle;
import com.interview.resource.repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

        public PaginatedResponse<Vehicle> getAllVehiclesPaginated(int page, int size) {
        // adjust for 0 based indexing 
        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Vehicle> pagedVehicles = vehicleRepository.findAll(pageable);
        
        PaginationMeta paginationMeta = new PaginationMeta(pagedVehicles.getNumber() + 1, pagedVehicles.getTotalPages(), pagedVehicles.getTotalElements(), pagedVehicles.getSize());

        return new PaginatedResponse<Vehicle>(
            pagedVehicles.getContent(),
            paginationMeta
        );
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        System.out.println(vehicle.getId());
        System.out.println(vehicle.getMake());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existingVehicle.setMake(vehicle.getMake());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setImage(vehicle.getImage());
        existingVehicle.setModelYear(vehicle.getModelYear());

        return vehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(Long id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
    }
}
