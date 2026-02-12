package com.interview.controller;

import com.interview.dto.ErrorResponse;
import com.interview.dto.ServicePackageRequest;
import com.interview.dto.ServicePackageResponse;
import com.interview.dto.StatusUpdateRequest;
import com.interview.dto.SubscribersResponse;
import com.interview.dto.SubscriptionRequest;
import com.interview.dto.ValidationErrorResponse;
import com.interview.service.ServicePackageService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing service package operations.
 *
 * <p>This controller provides endpoints for complete CRUD operations on service packages,
 * including subscription management and soft delete functionality. All endpoints follow RESTful
 * conventions and return appropriate HTTP status codes.
 *
 * <p><strong>Authentication & Authorization:</strong>
 * <ul>
 *   <li><strong>GET operations:</strong> Both ADMIN and USER roles can access</li>
 *   <li><strong>POST/PUT/PATCH/DELETE operations:</strong> Only ADMIN role can access</li>
 *   <li><strong>Authentication:</strong> JWT token required in Authorization header: "Bearer {token}"</li>
 * </ul>
 *
 * <p>Supported operations:
 * <ul>
 *   <li>POST /api/v1/service-packages - Create a new service package</li>
 *   <li>GET /api/v1/service-packages/{id} - Retrieve a service package by ID</li>
 *   <li>PUT /api/v1/service-packages/{id} - Update service package information</li>
 *   <li>GET /api/v1/service-packages?active=true - Retrieve service packages with filtering</li>
 *   <li>GET /api/v1/service-packages/paginated?active=true - Retrieve service packages with pagination</li>
 *   <li>PATCH /api/v1/service-packages/{id}/status - Activate/deactivate service package</li>
 *   <li>POST /api/v1/service-packages/{id}/subscribe - Subscribe customer to package</li>
 *   <li>DELETE /api/v1/service-packages/{id}/unsubscribe - Unsubscribe customer from package</li>
 *   <li>GET /api/v1/service-packages/{id}/subscribers - Get package subscribers</li>
 * </ul>
 *
 * <p>All endpoints include validation and proper error handling through the global exception handler.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service-packages")
public class ServicePackageController {

    private final ServicePackageService servicePackageService;

    /**
     * Create a new service package.
     */
    @Operation(summary = "Create a new service package", description = "Creates a new service package with pricing and description")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Service package created successfully",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ServicePackageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed or package name already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping
    public ResponseEntity<ServicePackageResponse> createServicePackage(@Valid @RequestBody ServicePackageRequest request) {
        log.info("Creating service package with name: {}", request.name());

        ServicePackageResponse response = servicePackageService.createServicePackage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get service package by ID (includes subscriber data).
     */
    @Operation(summary = "Get service package by ID",
               description = "Retrieves a service package by its unique identifier, including subscriber information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Service package found successfully",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ServicePackageResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<ServicePackageResponse> getServicePackageById(@PathVariable Long id) {
        log.info("Fetching service package with ID: {}", id);

        ServicePackageResponse response = servicePackageService.getServicePackageById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update service package.
     */
    @Operation(summary = "Update service package", description = "Updates service package information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Service package updated successfully",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ServicePackageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed or package name already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping("/{id}")
    public ResponseEntity<ServicePackageResponse> updateServicePackage(@PathVariable Long id, @Valid @RequestBody ServicePackageRequest request) {
        log.info("Updating service package with ID: {}", id);

        ServicePackageResponse response = servicePackageService.updateServicePackage(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all service packages with optional active filter.
     */
    @Operation(summary = "Get all service packages", description = "Retrieves all service packages with optional active status filtering")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Service packages retrieved successfully",
                                        content = @Content(mediaType = "application/json",
                                                           array = @ArraySchema(schema = @Schema(implementation = ServicePackageResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping
    public ResponseEntity<List<ServicePackageResponse>> getAllServicePackages(@RequestParam(required = false) Boolean active) {
        log.info("Fetching all service packages with active filter: {}", active);

        List<ServicePackageResponse> response = servicePackageService.getAllServicePackages(active);
        return ResponseEntity.ok(response);
    }

    /**
     * Get service packages with pagination and optional active filter.
     */
    @Operation(summary = "Get service packages with pagination",
               description = "Retrieves service packages with pagination support and optional active status filtering")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Service packages retrieved successfully with pagination",
                                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/paginated")
    public ResponseEntity<Page<ServicePackageResponse>> getServicePackagesWithPagination(@RequestParam(required = false) Boolean active,
        Pageable pageable) {
        log.info("Fetching service packages with pagination: {}, active filter: {}", pageable, active);

        Page<ServicePackageResponse> response = servicePackageService.getServicePackagesWithPagination(active, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate or deactivate a service package (soft delete).
     */
    @Operation(summary = "Update service package status", description = "Activate or deactivate a service package (soft delete)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Service package status updated successfully",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ServicePackageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PatchMapping("/{id}/status")
    public ResponseEntity<ServicePackageResponse> updateServicePackageStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        log.info("Updating service package {} status to: {}", id, request.active());

        ServicePackageResponse response = servicePackageService.updateServicePackageStatus(id, request.active());
        return ResponseEntity.ok(response);
    }

    /**
     * Subscribe a customer to a service package.
     */
    @Operation(summary = "Subscribe customer to service package", description = "Subscribe a customer to an active service package")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Customer subscribed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - customer already subscribed or package not active",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package or customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Void> subscribeCustomerToPackage(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        log.info("Subscribing customer {} to service package {}", request.customerId(), id);

        servicePackageService.subscribeCustomerToPackage(id, request.customerId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Unsubscribe a customer from a service package.
     */
    @Operation(summary = "Unsubscribe customer from service package", description = "Unsubscribe a customer from a service package")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Customer unsubscribed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - customer not subscribed to package",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package or customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping("/{id}/customers/{customerId}")
    public ResponseEntity<Void> unsubscribeCustomerFromPackage(@PathVariable Long id, @PathVariable Long customerId) {
        log.info("Unsubscribing customer {} from service package {}", customerId, id);

        servicePackageService.unsubscribeCustomerFromPackage(id, customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all subscribers of a service package.
     */
    @Operation(summary = "Get service package subscribers",
               description = "Retrieves count and basic information of all customers subscribed to a service package")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = SubscribersResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Service package not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}/subscribers")
    public ResponseEntity<SubscribersResponse> getServicePackageSubscribers(@PathVariable Long id) {
        log.info("Fetching subscribers for service package {}", id);

        SubscribersResponse response = servicePackageService.getServicePackageSubscribers(id);
        return ResponseEntity.ok(response);
    }
}