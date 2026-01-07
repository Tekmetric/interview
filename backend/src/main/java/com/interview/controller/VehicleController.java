package com.interview.controller;

import com.interview.dto.VehicleDTO;
import com.interview.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles in the repair shop")
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
    @Operation(
        summary = "Get all vehicles",
        description = "Retrieves a paginated list of vehicles with optional VIN filtering. " +
                      "Supports partial, case-insensitive VIN search. " +
                      "Default page size: 20, sorted by modelYear descending (newest first)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles",
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(
            @Parameter(description = "Filter by partial VIN (case-insensitive), optional", example = "1HGC")
            @RequestParam(required = false) String vin,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "modelYear", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(vin, pageable));
    }

    /**
     * Retrieves a single vehicle by its unique ID.
     * Returns 404 if vehicle not found.
     */
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a single vehicle by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle found",
                     content = @Content(schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> getVehicleById(
            @Parameter(description = "Unique vehicle ID", example = "1", required = true)
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    /**
     * Creates a new vehicle.
     * Validates VIN uniqueness and customer existence.
     * Returns 201 Created with the created vehicle.
     */
    @Operation(
        summary = "Create new vehicle",
        description = "Creates a new vehicle. VIN must be unique and customer must exist. All fields are validated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully",
                     content = @Content(schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input, VIN already exists, or customer not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Vehicle details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = VehicleDTO.class))
            )
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO created = vehicleService.createVehicle(vehicleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing vehicle by ID.
     * Validates VIN uniqueness and customer existence.
     * Returns 404 if vehicle not found.
     */
    @Operation(
        summary = "Update vehicle",
        description = "Updates an existing vehicle by ID. VIN must be unique if changed and customer must exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                     content = @Content(schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input, VIN already exists, or customer not found", content = @Content),
        @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @Parameter(description = "Unique vehicle ID", example = "1", required = true)
            @PathVariable Long vehicleId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated vehicle details",
                required = true,
                content = @Content(schema = @Schema(implementation = VehicleDTO.class))
            )
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, vehicleDTO));
    }

    /**
     * Deletes a vehicle by ID.
     * Returns 204 No Content on success, 404 if vehicle not found.
     */
    @Operation(
        summary = "Delete vehicle",
        description = "Deletes a vehicle by its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "Unique vehicle ID", example = "1", required = true)
            @PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
