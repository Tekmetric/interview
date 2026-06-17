package com.interview.controller;

import com.interview.dto.ErrorResponse;
import com.interview.dto.ValidationErrorResponse;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.filter.VehicleFilter;
import com.interview.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing vehicle operations.
 *
 * <p>This controller provides endpoints for complete CRUD operations on vehicles,
 * including their associated customer information. All endpoints follow RESTful
 * conventions and return appropriate HTTP status codes.
 *
 * <p><strong>Authentication & Authorization:</strong>
 * <ul>
 *   <li><strong>GET operations:</strong> Both ADMIN and USER roles can access</li>
 *   <li><strong>POST/PUT/DELETE operations:</strong> Only ADMIN role can access</li>
 *   <li><strong>Authentication:</strong> JWT token required in Authorization header: "Bearer {token}"</li>
 * </ul>
 *
 * <p>Supported operations:
 * <ul>
 *   <li>POST /api/v1/vehicles - Create a new vehicle</li>
 *   <li>GET /api/v1/vehicles/{id} - Retrieve a vehicle by ID</li>
 *   <li>GET /api/v1/vehicles - Retrieve all vehicles</li>
 *   <li>GET /api/v1/vehicles?page=0&size=10&sort=year,desc - Retrieve vehicles with pagination</li>
 *   <li>PUT /api/v1/vehicles/{id} - Update vehicle information</li>
 *   <li>DELETE /api/v1/vehicles/{id} - Delete vehicle</li>
 * </ul>
 *
 * <p>All endpoints include validation and proper error handling through the global exception handler.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Create a new vehicle.
     */
    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle for an existing customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed or VIN already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        log.info("Creating vehicle with VIN: {} for customer ID: {}", request.vin(), request.customerId());

        VehicleResponse response = vehicleService.createVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get vehicle by ID (includes customer data).
     */
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a vehicle by its unique identifier, including customer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle found successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        log.info("Fetching vehicle with ID: {}", id);

        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vehicles (includes customer data).
     */
    @Operation(summary = "Get all vehicles", description = "Retrieves all vehicles with their customer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully",
                     content = @Content(mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = VehicleResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        log.info("Fetching all vehicles");

        List<VehicleResponse> response = vehicleService.getAllVehicles();
        return ResponseEntity.ok(response);
    }

    /**
     * Get vehicles with pagination (includes customer data).
     */
    @Operation(summary = "Get vehicles with pagination", description = "Retrieves vehicles with pagination support, including customer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully with pagination",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<VehicleResponse>> getVehiclesWithPagination(Pageable pageable) {
        log.info("Fetching vehicles with pagination: {}", pageable);

        Page<VehicleResponse> response = vehicleService.getVehiclesWithPagination(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Search vehicles with filters and pagination (includes customer data).
     */
    @Operation(summary = "Search vehicles with filters", description = "Search vehicles using various filters with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicles search completed successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<VehicleResponse>> searchVehicles(
        @RequestParam(required = false) Long customerId,
        @RequestParam(required = false) String vin,
        @RequestParam(required = false) String make,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) Integer minYear,
        @RequestParam(required = false) Integer maxYear,
        @RequestParam(required = false) String customerEmail,
        @RequestParam(required = false) String customerName,
        Pageable pageable) {

        log.info("Searching vehicles with filters - customerId: {}, vin: {}, make: {}, model: {}",
            customerId, vin, make, model);

        VehicleFilter filter = VehicleFilter.builder()
            .customerId(customerId)
            .vin(vin)
            .make(make)
            .model(model)
            .minYear(minYear)
            .maxYear(maxYear)
            .customerEmail(customerEmail)
            .customerName(customerName)
            .build();

        Page<VehicleResponse> response = vehicleService.searchVehicles(filter, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update vehicle.
     */
    @Operation(summary = "Update vehicle", description = "Updates vehicle information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed or VIN already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle or customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        log.info("Updating vehicle with ID: {}", id);

        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete vehicle.
     */
    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.info("Deleting vehicle with ID: {}", id);

        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}