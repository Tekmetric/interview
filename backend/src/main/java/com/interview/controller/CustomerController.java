package com.interview.controller;

import com.interview.dto.CustomerRequest;
import com.interview.dto.CustomerResponse;
import com.interview.dto.ErrorResponse;
import com.interview.dto.ValidationErrorResponse;
import com.interview.service.CustomerService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing customer operations.
 *
 * <p>This controller provides endpoints for complete CRUD operations on customers,
 * including their associated profile information. All endpoints follow RESTful
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
 *   <li>POST /api/v1/customers - Create a new customer with optional profile</li>
 *   <li>GET /api/v1/customers/{id} - Retrieve a customer by ID</li>
 *   <li>GET /api/v1/customers - Retrieve all customers</li>
 *   <li>GET /api/v1/customers/paginated?page=0&size=10&sort=firstName,asc - Retrieve customers with pagination</li>
 *   <li>PUT /api/v1/customers/{id} - Update customer and profile information</li>
 *   <li>DELETE /api/v1/customers/{id} - Delete customer and associated profile</li>
 * </ul>
 *
 * <p><strong>Optimistic Locking:</strong>
 * The Customer entity uses optimistic locking with a version field to handle concurrent updates.
 * When updating a customer:
 * <ul>
 *   <li>Include the current version from GET response in your PUT request</li>
 *   <li>If another user modified the customer, you'll receive HTTP 409 (Conflict)</li>
 *   <li>Refresh the data and retry with the new version</li>
 * </ul>
 *
 * <p>All endpoints include validation and proper error handling through the global exception handler.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Create a new customer with profile.
     */
    @Operation(summary = "Create a new customer", description = "Creates a new customer with optional profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Customer with email already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        log.info("Creating customer with email: {}", request.email());

        CustomerResponse response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get customer by ID (includes profile data).
     */
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique identifier, including profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        log.info("Fetching customer with ID: {}", id);

        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all customers (includes profile data).
     */
    @Operation(summary = "Get all customers", description = "Retrieves all customers with their profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                     content = @Content(mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.info("Fetching all customers");

        List<CustomerResponse> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    /**
     * Get customers with pagination (includes profile data).
     */
    @Operation(summary = "Get customers with pagination", description = "Retrieves customers with pagination support, including profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully with pagination",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<CustomerResponse>> getCustomersWithPagination(Pageable pageable) {
        log.info("Fetching customers with pagination: {}", pageable);

        Page<CustomerResponse> response = customerService.getCustomersWithPagination(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer and profile.
     */
    @Operation(summary = "Update customer", description = "Updates customer information and profile data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Customer with email already exists",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    /**
     * Delete customer (profile deleted automatically).
     */
    @Operation(summary = "Delete customer", description = "Deletes a customer and their associated profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required - missing or invalid JWT token",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);

        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}