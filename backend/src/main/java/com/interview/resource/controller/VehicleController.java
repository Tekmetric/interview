package com.interview.resource.controller;

import com.interview.resource.service.VehicleService;
import com.interview.resource.model.PaginatedResponse;
import com.interview.resource.model.PaginationMeta;
import com.interview.resource.model.Vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(value = {"/api/vehicles", "/api/vehicles/"})
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Get all vehicles
    @GetMapping
    public ResponseEntity<PaginatedResponse<Vehicle>> getAllVehicles(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "0") int size) {
        
       // If both page and size are 0, return all vehicles without pagination
        if (page == 0 && size == 0) {
            // Fetch all vehicles
            List<Vehicle> allVehicles = vehicleService.getAllVehicles();  // fetch all vehicles without pagination

            // Wrap in a PaginatedResponse and create holistic metadata
            PaginationMeta paginationMeta = new PaginationMeta(1, 1, allVehicles.size(), allVehicles.size());
            PaginatedResponse<Vehicle> response = new PaginatedResponse<Vehicle>(allVehicles, paginationMeta);

            return ResponseEntity.ok(response);
        }
        
        // Otherwise, return paginated vehicles
        PaginatedResponse<Vehicle> vehiclePage = vehicleService.getAllVehiclesPaginated(page, size);  // fetch vehicles with pagination

        return ResponseEntity.ok(vehiclePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Vehicle> createVehicle(
        @RequestPart("vin") String vin,
        @RequestPart("make") String make,
        @RequestPart("model") String model,
        @RequestPart("modelYear") String modelYear,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Vehicle vehicle = vehicleService.buildVehicleFromFormData(new Vehicle(), vin, make, model, modelYear, image);
            Vehicle createdVehicle = vehicleService.createVehicle(vehicle);

            // return 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(path = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Vehicle> updateVehicle(
        @PathVariable Long id,
        @RequestPart("vin") String vin,
        @RequestPart("make") String make,
        @RequestPart("model") String model,
        @RequestPart("modelYear") String modelYear,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Optional<Vehicle> existingVehicle = vehicleService.getVehicleById(id);

            if (existingVehicle.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Vehicle updatedVehicle = vehicleService.buildVehicleFromFormData(
                existingVehicle.get(), vin, make, model, modelYear, image
            );

            Vehicle saved = vehicleService.updateVehicle(id, updatedVehicle);

            // return 200 Updated
            return ResponseEntity.status(HttpStatus.OK).body(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);

        // return 204 No Content
        return ResponseEntity.noContent().build();
    }
}
