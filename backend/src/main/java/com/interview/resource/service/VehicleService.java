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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.Path;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        // TODO add search by query
        return vehicleRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public PaginatedResponse<Vehicle> getAllVehiclesPaginated(int page, int size, String query) {
        // adjust for 0 based indexing
        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Vehicle> pagedVehicles;

        if (query != null && !query.trim().isEmpty()) {
            pagedVehicles = vehicleRepository.searchByQuery(query, pageable);
        } else {
            pagedVehicles = vehicleRepository.findAll(pageable);
        }

        PaginationMeta paginationMeta = new PaginationMeta(pagedVehicles.getNumber() + 1, pagedVehicles.getTotalPages(),
                pagedVehicles.getTotalElements(), pagedVehicles.getSize());

        return new PaginatedResponse<Vehicle>(pagedVehicles.getContent(), paginationMeta);
    }

    public Vehicle buildVehicleFromFormData(Vehicle vehicle, String vin, String make, String model,
            String modelYear, MultipartFile image, Boolean removeImage) throws IOException {
        vehicle.setVin(vin);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setModelYear(Integer.parseInt(modelYear));

        // TODO remove hard coded localhost and add S3 uploads
        if (removeImage) {
            vehicle.setImage(null);
        } else if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            vehicle.setImage("http://localhost:8080/uploads/" + fileName);
        }

        return vehicle;
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        System.out.println(vehicle.getImage());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
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
