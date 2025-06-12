package com.interview.controller;

import com.interview.dto.CustomerRequestDTO;
import com.interview.dto.CustomerResponseDTO;
import com.interview.dto.CustomerSummaryDTO;
import com.interview.dto.PaginationRequestDTO;
import com.interview.service.CustomerService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CustomerController handles all customer-related API requests. It provides endpoints for CRUD
 * operations, and pagination.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public ResponseEntity<List<CustomerSummaryDTO>> getAllCustomers() {
    log.info("GET /api/v1/customers - Fetching all customers");
    List<CustomerSummaryDTO> customers = customerService.getAllCustomers();
    return ResponseEntity.ok(customers);
  }

  @GetMapping("/paginated")
  public ResponseEntity<Page<CustomerSummaryDTO>> getAllCustomersPaginated(
      @Valid PaginationRequestDTO pagination) {

    log.info("GET /api/v1/customers/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}",
        pagination.getPage(), pagination.getSize(), pagination.getSortBy(), pagination.getSortDir());

    Sort sort = pagination.getSortDir().equalsIgnoreCase("desc") ?
        Sort.by(pagination.getSortBy()).descending() : Sort.by(pagination.getSortBy()).ascending();
    Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

    Page<CustomerSummaryDTO> customers = customerService.getAllCustomers(pageable);
    return ResponseEntity.ok(customers);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
    log.info("GET /api/v1/customers/{} - Fetching customer by id", id);
    CustomerResponseDTO customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(customer);
  }

  @GetMapping("/{id}/with-vehicles")
  public ResponseEntity<CustomerResponseDTO> getCustomerByIdWithVehicles(@PathVariable Long id) {
    log.info("GET /api/v1/customers/{}/with-vehicles - Fetching customer with vehicles", id);
    CustomerResponseDTO customer = customerService.getCustomerByIdWithVehicles(id);
    return ResponseEntity.ok(customer);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(@PathVariable String email) {
    log.info("GET /api/v1/customers/email/{} - Fetching customer by email", email);
    CustomerResponseDTO customer = customerService.getCustomerByEmail(email);
    return ResponseEntity.ok(customer);
  }

  @PostMapping
  public ResponseEntity<CustomerResponseDTO> createCustomer(
      @Valid @RequestBody CustomerRequestDTO customerRequest) {
    log.info("POST /api/v1/customers - Creating new customer with email: {}",
        customerRequest.email());
    CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponseDTO> updateCustomer(
      @PathVariable Long id,
      @Valid @RequestBody CustomerRequestDTO customerRequest) {
    log.info("PUT /api/v1/customers/{} - Updating customer", id);
    CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerRequest);
    return ResponseEntity.ok(updatedCustomer);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    log.info("DELETE /api/v1/customers/{} - Deleting customer", id);
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getTotalCustomers() {
    log.info("GET /api/v1/customers/count - Getting total customer count");
    long count = customerService.getTotalCustomers();
    return ResponseEntity.ok(count);
  }

}
