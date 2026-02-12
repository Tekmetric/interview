package com.interview.controllers;

import com.interview.dtos.*;
import com.interview.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicle Management", description = "API for managing vehicles")
public class VehicleController {
    private final VehicleService service;

    @Operation(summary = "Get a vehicle by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getById(
            @Parameter(description = "ID of the vehicle to retrieve") @PathVariable @NotNull Long id) {
        VehicleResponseDTO vehicle = service.findById(id);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(summary = "Get a paged list of all vehicles", description = "Returns a paginated list of all vehicles. Use the page and size parameters to control the response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of vehicles retrieved successfully")
    })
    @GetMapping
    public PageResponse<VehicleResponseDTO> getAll(@ParameterObject Pageable pageable) {
        return PageResponse.from(service.findAll(pageable));
    }

    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle record with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
    })
    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody VehicleRequestDTO dto) {
        VehicleResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update an existing vehicle", description = "Updates all fields of an existing vehicle record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public VehicleResponseDTO update(
            @Parameter(description = "ID of the vehicle to update") @PathVariable @NotNull Long id,
            @Valid @RequestBody VehicleRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Partially update a vehicle", description = "Updates a specific field or fields of an existing vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public VehicleResponseDTO patch(
            @Parameter(description = "ID of the vehicle to patch") @PathVariable @NotNull Long id,
            @Valid @RequestBody VehiclePatchDTO dto) {
        return service.patch(id, dto);
    }

    @Operation(summary = "Delete a vehicle by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the vehicle to delete") @PathVariable @NotNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a vehicle by its VIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/vin/{vin}")
    public ResponseEntity<VehicleResponseDTO> findByVin(
            @Parameter(description = "VIN of the vehicle to retrieve", example = "1HGCM82633A123456") @PathVariable @NotBlank String vin) {
        VehicleResponseDTO vehicle = service.findByVin(vin);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(summary = "Search vehicles with criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles found matching criteria"),
    })
    @PostMapping("/search")
    public PageResponse<VehicleResponseDTO> searchVehicles(
            @ParameterObject Pageable pageable,
            @Valid @RequestBody VehicleSearchCriteriaDTO criteria) {

        return PageResponse.from(service.search(pageable, criteria));
    }
}