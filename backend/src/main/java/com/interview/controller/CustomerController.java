package com.interview.controller;

import com.interview.dto.CustomerDTO;
import com.interview.dto.VehicleDTO;
import com.interview.service.CustomerService;
import com.interview.service.VehicleService;
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
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @PageableDefault(size = 20, sort = "firstName") Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(email, firstName, lastName, pageable));
    }

    /**
     * Retrieves a single customer by their unique ID.
     * Returns 404 if customer not found.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    /**
     * Creates a new customer.
     * Validates email uniqueness.
     * Returns 201 Created with the created customer.
     */
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO created = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing customer by ID.
     * Validates email uniqueness.
     * Returns 404 if customer not found.
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerDTO));
    }

    /**
     * Deletes a customer by ID.
     * Cascades delete to associated vehicles.
     * Returns 204 No Content on success, 404 if customer not found.
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all vehicles belonging to a specific customer.
     * Returns empty list if customer has no vehicles.
     */
    @GetMapping("/{customerId}/vehicles")
    public ResponseEntity<List<VehicleDTO>> getCustomerVehicles(@PathVariable Long customerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByCustomerId(customerId));
    }
}
