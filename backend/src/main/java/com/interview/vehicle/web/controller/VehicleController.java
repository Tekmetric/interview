package com.interview.vehicle.web.controller;

import com.interview.util.QueryPageableParamsValidator;
import com.interview.vehicle.VehicleService;
import com.interview.vehicle.model.VehicleCreate;
import com.interview.vehicle.model.VehicleId;
import com.interview.vehicle.model.VehicleUpdate;
import com.interview.vehicle.web.model.CreateVehicleRequest;
import com.interview.vehicle.web.model.UpdateVehicleRequest;
import com.interview.vehicle.web.model.VehicleDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(
            summary = "Get list of vehicles",
            description = "Retrieves a paginated list of vehicles")
    public ResponseEntity<Page<VehicleDTO>> getVehicle(Pageable pageable) {
        QueryPageableParamsValidator.validate(pageable);

        var vehicles = vehicleService.getAll(pageable)
                .map(VehicleDTO::fromEntity);

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    @Operation(
            summary = "Get a vehicle by ID",
            description = "Retrieves a specific vehicle by its ID")
    public ResponseEntity<VehicleDTO> getVehicle(@Valid @PathVariable("vehicleId") VehicleId vehicleId) {
        var vehicle = vehicleService.get(vehicleId);

        return ResponseEntity.ok(VehicleDTO.fromEntity(vehicle));
    }

    @PostMapping
    @Operation(
            summary = "Create a new vehicle",
            description = "Creates a new vehicle based on CreateVehicleRequest")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        var vehicle = vehicleService.create(VehicleCreate.builder()
                .type(request.getType())
                .fabricationYear(Year.of(request.getFabricationYear()))
                .make(request.getMake())
                .model(request.getModel())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VehicleDTO.fromEntity(vehicle));
    }

    @PutMapping("/{vehicleId}")
    @Operation(
            summary = "Update a vehicle",
            description = "Updates an existing vehicle by its ID based on CreateVehicleRequest")
    public ResponseEntity<VehicleDTO> updateVehicle(@Valid @PathVariable("vehicleId") VehicleId vehicleId, @Valid @RequestBody UpdateVehicleRequest request) {
        var vehicle = vehicleService.update(vehicleId, VehicleUpdate.builder()
                .type(request.getType())
                .fabricationYear(Year.of(request.getFabricationYear()))
                .make(request.getMake())
                .model(request.getModel())
                .build());

        return ResponseEntity.ok(VehicleDTO.fromEntity(vehicle));
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(
            summary = "Delete vehicle",
            description = "Deletes a vehicle by its ID")
    public ResponseEntity<Void> deleteVehicle(@Valid @PathVariable("vehicleId") VehicleId vehicleId) {
        vehicleService.delete(vehicleId);

        return ResponseEntity.noContent().build();
    }

}
