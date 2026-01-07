package com.interview.controller;

import com.interview.dto.VehicleDTO;
import com.interview.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Retrieves all vehicles with pagination and optional partial VIN search.
     * Supports filtering by VIN using case-insensitive partial match.
     * Default sort: modelYear DESC (newest first).
     * 
     * Note: I implemented partial VIN search using LIKE %value% for convenience.
     * In a high-scale production environment with millions of rows, I would replace this with
     * a Full-Text Search engine (Elasticsearch) or ensure we only support Prefix Search (value%)
     * so the database can utilize the B-Tree index effectively.
     */
    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(
            @RequestParam(required = false) String vin,
            @PageableDefault(size = 20, sort = "modelYear", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(vin, pageable));
    }

    /**
     * Retrieves a single vehicle by its unique ID.
     * Returns 404 if vehicle not found.
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    /**
     * Creates a new vehicle.
     * Validates VIN uniqueness and customer existence.
     * Returns 201 Created with the created vehicle.
     */
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO created = vehicleService.createVehicle(vehicleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing vehicle by ID.
     * Validates VIN uniqueness and customer existence.
     * Returns 404 if vehicle not found.
     */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, vehicleDTO));
    }

    /**
     * Deletes a vehicle by ID.
     * Returns 204 No Content on success, 404 if vehicle not found.
     */
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
