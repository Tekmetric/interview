package com.interview.controller;

import com.interview.dto.PaginatedResponse;
import com.interview.dto.VehicleFilterRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.service.VehicleService;
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
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<VehicleResponse>> getAllVehicles(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer registrationYear,
            @RequestParam(required = false) Integer registrationYearFrom,
            @RequestParam(required = false) Integer registrationYearTo,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "Page size must be positive") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
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

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByOwner(@PathVariable @Positive(message = "Owner ID must be a positive number") Long ownerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(ownerId));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        return new ResponseEntity<>(vehicleService.createVehicle(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id,
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable @Positive(message = "Vehicle ID must be a positive number") Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
