package com.interview.controller;

import com.interview.dto.CustomerDTO;
import com.interview.dto.VehicleDTO;
import com.interview.service.CustomerService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers in the repair shop")
public class CustomerController {

    private final CustomerService customerService;
    private final VehicleService vehicleService;

    /**
     * Retrieves all customers with pagination and optional partial search.
     * Supports filtering by email, firstName, or lastName using case-insensitive partial match.
     * Default sort: firstName ASC.
     * 
     * Note: I implemented partial search using LIKE %value% for convenience.
     * In a high-scale production environment with millions of rows, I would replace this with
     * a Full-Text Search engine (Elasticsearch) or ensure we only support Prefix Search (value%)
     * so the database can utilize the B-Tree index effectively.
     */
    @Operation(
        summary = "Get all customers",
        description = "Retrieves a paginated list of customers with optional filtering. " +
                      "Supports partial, case-insensitive search by email, firstName, or lastName. " +
                      "Default page size: 20, sorted by firstName ascending."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customers",
                     content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @Parameter(description = "Filter by partial email (case-insensitive)", example = "john")
            @RequestParam(required = false) String email,
            @Parameter(description = "Filter by partial first name (case-insensitive)", example = "Jane")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Filter by partial last name (case-insensitive)", example = "Smith")
            @RequestParam(required = false) String lastName,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "firstName") Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(email, firstName, lastName, pageable));
    }

    /**
     * Retrieves a single customer by their unique ID.
     * Returns 404 if customer not found.
     */
    @Operation(summary = "Get customer by ID", description = "Retrieves a single customer by their unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found",
                     content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Unique customer ID", example = "1", required = true)
            @PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    /**
     * Creates a new customer.
     * Validates email uniqueness.
     * Returns 201 Created with the created customer.
     */
    @Operation(
        summary = "Create new customer",
        description = "Creates a new customer. Email must be unique. All fields are validated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                     content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Customer details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = CustomerDTO.class))
            )
            @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO created = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing customer by ID.
     * Validates email uniqueness.
     * Returns 404 if customer not found.
     */
    @Operation(
        summary = "Update customer",
        description = "Updates an existing customer by ID. Email must be unique if changed."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                     content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Unique customer ID", example = "1", required = true)
            @PathVariable Long customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated customer details",
                required = true,
                content = @Content(schema = @Schema(implementation = CustomerDTO.class))
            )
            @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerDTO));
    }

    /**
     * Deletes a customer by ID.
     * Cascades delete to associated vehicles.
     * Returns 204 No Content on success, 404 if customer not found.
     */
    @Operation(
        summary = "Delete customer",
        description = "Deletes a customer and all associated vehicles (cascade delete)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Unique customer ID", example = "1", required = true)
            @PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all vehicles belonging to a specific customer.
     * Returns empty list if customer has no vehicles.
     */
    @Operation(
        summary = "Get customer's vehicles",
        description = "Retrieves all vehicles owned by a specific customer"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles",
                     content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/{customerId}/vehicles")
    public ResponseEntity<List<VehicleDTO>> getCustomerVehicles(
            @Parameter(description = "Unique customer ID", example = "1", required = true)
            @PathVariable Long customerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByCustomerId(customerId));
    }
}
