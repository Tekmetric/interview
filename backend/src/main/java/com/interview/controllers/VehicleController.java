package com.interview.controllers;

import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleRequestDTO;
import com.interview.dtos.VehicleResponseDTO;
import com.interview.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<VehicleResponseDTO> getById(
            @Parameter(description = "Vehicle ID") @PathVariable @NotNull Long id) {
        VehicleResponseDTO vehicle = service.findById(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    @Operation(summary = "Get all vehicles")
    public Page<VehicleResponseDTO> getAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @PostMapping
    @Operation(summary = "Create new vehicle", description = "Creates a new vehicle record")
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody VehicleRequestDTO dto) {
        VehicleResponseDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle")
    public VehicleResponseDTO update(@PathVariable @NotNull Long id, @Valid @RequestBody VehicleRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update vehicle")
    public VehicleResponseDTO patch(@PathVariable @NotNull Long id, @RequestBody VehiclePatchDTO dto) {
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vin/{vin}")
    @Operation(summary = "Get vehicle by VIN")
    public ResponseEntity<Optional<VehicleResponseDTO>> findByVin(@PathVariable @NotBlank String vin) {
        Optional<VehicleResponseDTO> vehicle = service.findByVin(vin);
        return ResponseEntity.ok(vehicle);
    }
}
