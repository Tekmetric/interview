package com.interview.controller;

import com.interview.config.CustomerConfig;
import com.interview.dto.*;
import com.interview.entity.Customer;
import com.interview.mapper.CustomerMapper;
import com.interview.service.EventPublisher;
import com.interview.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerMapper customerMapper;
    private final CustomerConfig customerConfig;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<PagedResponse<CustomerResponse>> getAllCustomers(
            @RequestParam(required = false, defaultValue = "lastName", name = "sort") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false, name = "lastname") String lastName,
            @RequestParam(required = false, name = "firstname") String firstName) {

        return ResponseEntity.ok(customerService.getCustomers(sort, page, size, lastName, firstName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        Customer customer = findCustomerByIdOrThrow(id);
        return ResponseEntity.ok(customerMapper.toDto(customer));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            UriComponentsBuilder uriBuilder) {

        int lastNameCountThreshold = customerConfig.getThreshold();
        if (customerService.countByLastName(request.getLastName()) == lastNameCountThreshold) {
            String message = "Can not create customer. Same last name should not appear more than " + lastNameCountThreshold + " times";
            log.warn(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        Customer customer = customerService.createCustomer(request);

        // Publish message for email notification
        eventPublisher.publishCustomerCreatedEvent(customer);

        URI uri = uriBuilder.path("/api/customers/{id}").buildAndExpand(customer.getId()).toUri();

        // return location, good rest practice
        return ResponseEntity.created(uri).body(customerMapper.toDto(customer));
    }

    @PutMapping("/{customer_id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable(name = "customer_id") UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        Customer existingCustomer = findCustomerByIdOrThrow(id);

        int customerVersionInDB = existingCustomer.getVersion();
        int customerVersionInRequest = request.getVersion();

        // Apply the updates from request to existingCustomer
        customerMapper.update(request, existingCustomer);

        try {
            Customer updatedCustomer = customerService.updateCustomer(existingCustomer, customerVersionInDB, customerVersionInRequest);
            return ResponseEntity.ok(customerMapper.toDto(updatedCustomer));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Update conflict")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw e;
        }
    }

    // 1. happy case 204 no content, 2. non-exist customer 404
    @DeleteMapping("/{customer_id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable(name = "customer_id") UUID id) {
        Customer existingCustomer = findCustomerByIdOrThrow(id);
        customerService.deleteCustomer(existingCustomer);
        return ResponseEntity.noContent().build();
    }

    // 1. happy case 204 no content, 2. 401 unauthorized, 3.non-exist customer 404
    // Action-based updates
    @PostMapping("/{customer_id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable(name = "customer_id") UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        Customer existingCustomer = findCustomerByIdOrThrow(id);

        // With BCrypt (Spring Security's default), hashing the same password twice produces different hashes
        // (due to the random salt), so equals() will almost never match.
        if (!passwordEncoder.matches(request.getOldPassword(), existingCustomer.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Encode the new password and save
        existingCustomer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerService.updateCustomer(existingCustomer);

        return ResponseEntity.noContent().build();
    }

    private Customer findCustomerByIdOrThrow(UUID id) {
        return customerService.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
