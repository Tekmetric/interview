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
// TODO EXPLAIN: mapping CustomerController to the customer endpoint
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
        // TODO EXPLAIN: orElse, var vs customer
        Customer customer = customerService.findCustomerById(id).orElse(null);

        if (customer == null) {
            // TODO EXPLAIN: return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            // use static factory methods
            return ResponseEntity.notFound().build();
        }

        // TODO EXPLAIN: return new ResponseEntity<>(customerDto, HttpStatus.OK);
        return ResponseEntity.ok(customerMapper.toDto(customer));
    }

    // TODO EXPLAIN: 1. happy case 200 2. non-exist customer, 404
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

        // TODO EXPLAIN: return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDto(customer));
        // return location, good rest practice
        return ResponseEntity.created(uri).body(customerMapper.toDto(customer));
    }

    @PutMapping("/{customer_id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable(name = "customer_id") UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        Customer existingCustomer = customerService.findCustomerById(id).orElse(null);

        if (existingCustomer == null) {
            return ResponseEntity.notFound().build();
        }

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

    // TODO EXPLAIN: 1. happy case 204 no content 2. non-exist customer, 404
    @DeleteMapping("/{customer_id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable(name = "customer_id") UUID id) {
        Customer existingCustomer = customerService.findCustomerById(id).orElse(null);
        if (existingCustomer == null) {
            return ResponseEntity.notFound().build();
        }

        customerService.deleteCustomer(existingCustomer);

        return ResponseEntity.noContent().build();
    }

    // TODO EXPLAIN: 1. happy case 204 no content 2. 401 unauthorized 3.non-exist customer, 404
    // Action-based updates
    @PostMapping("/{customer_id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable(name = "customer_id") UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        Customer existingCustomer = customerService.findCustomerById(id).orElse(null);
        if (existingCustomer == null) {
            return ResponseEntity.notFound().build();
        }

        // With BCrypt (Spring Security's default), hashing the same password twice produces different hashes
        // (due to the random salt), so equals() will almost never match.
        if (!passwordEncoder.matches(request.getOldPassword(), existingCustomer.getPassword())) {
            // TODO EXPLAIN: 400 Bad Request vs UNAUTHORIZED
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO EXPLAIN: no need to user mapper, which is for large/complex objects
        // Encode the new password and save
        existingCustomer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerService.updateCustomer(existingCustomer);

        return ResponseEntity.noContent().build();
    }

    // TODO EXPLAIN: local
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
