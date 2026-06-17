package com.interview.controller;

import com.interview.dto.PaginatedResponse;
import com.interview.dto.VehicleFilterRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vehicles", description = "Vehicle management endpoints")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Get all vehicles", description = "Retrieve all vehicles with pagination and filtering. Requires ADMIN role.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<VehicleResponse>> getAllVehicles(
            @Parameter(description = "Filter by brand (case-insensitive partial match)") @RequestParam(required = false) String brand,
            @Parameter(description = "Filter by model (case-insensitive partial match)") @RequestParam(required = false) String model,
            @Parameter(description = "Filter by exact registration year") @RequestParam(required = false) Integer registrationYear,
            @Parameter(description = "Filter by minimum registration year") @RequestParam(required = false) Integer registrationYearFrom,
            @Parameter(description = "Filter by maximum registration year") @RequestParam(required = false) Integer registrationYearTo,
            @Parameter(description = "Filter by license plate (case-insensitive partial match)") @RequestParam(required = false) String licensePlate,
            @Parameter(description = "Filter by owner ID") @RequestParam(required = false) Long ownerId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Positive(message = "Page size must be positive") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        VehicleFilterRequest filter = VehicleFilterRequest.builder()
                .brand(brand)
                .model(model)
                .registrationYear(registrationYear)
                .registrationYearFrom(registrationYearFrom)
                .registrationYearTo(registrationYearTo)
                .licensePlate(licensePlate)
                .ownerId(ownerId)
                .build();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(vehicleService.getAllVehicles(filter, pageable));
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieve a specific vehicle by ID. Requires ADMIN or CUSTOMER role. Customers can only access their own vehicles.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @Operation(summary = "Get vehicles by owner", description = "Retrieve all vehicles owned by a specific user. Requires ADMIN or CUSTOMER role. Customers can only access their own vehicles.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByOwner(@PathVariable @Positive(message = "Owner ID must be a positive number") Long ownerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(ownerId));
    }

    @Operation(summary = "Create vehicle", description = "Create a new vehicle. Requires CUSTOMER role. Owner is automatically set from JWT token. License plate must be unique.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        return new ResponseEntity<>(vehicleService.createVehicle(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update vehicle", description = "Update an existing vehicle. Requires CUSTOMER role. Only the vehicle owner can update their own vehicles. License plate must be unique.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id,
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @Operation(summary = "Delete vehicle", description = "Delete a vehicle by ID. Requires CUSTOMER role. Only the vehicle owner can delete their own vehicles.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
