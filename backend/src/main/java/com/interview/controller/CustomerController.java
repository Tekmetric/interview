package com.interview.controller;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.dto.request.CreateCustomerRequest;
import com.interview.dto.request.UpdateCustomerRequest;
import com.interview.dto.response.CustomerResponse;
import com.interview.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Create, read, update, and delete dealership customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Customer created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody final CreateCustomerRequest request) {
        final CustomerResponse response = customerService.create(request);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping
    @Operation(summary = "List all customers (paginated)")
    @ApiResponse(responseCode = "200", description = "Paginated list of customers")
    public ResponseEntity<Page<CustomerResponse>> findAll(
            @PageableDefault(size = 20, sort = "dateCreated", direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<CustomerResponse> update(
            @PathVariable final UUID id,
            @Valid @RequestBody final UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Customer deleted"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
