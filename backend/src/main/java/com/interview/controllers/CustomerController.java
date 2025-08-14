package com.interview.controllers;

import com.interview.config.CustomerConfig;
import com.interview.dtos.*;
import com.interview.entity.Customer;
import com.interview.mappers.CustomerMapper;
import com.interview.repositories.CustomerRepository;
import com.interview.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
// TODO EXPLAIN: mapping CustomerController to the customer endpoint
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerConfig customerConfig;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<CustomerPageDto> getAllCustomers(
            @RequestParam(required = false, defaultValue = "lastName", name = "sort") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        return ResponseEntity.ok(customerService.getCustomers(sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable UUID id) {
        // TODO EXPLAIN: orElse, var vs customer
        Customer customer = customerRepository.findById(id).orElse(null);

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
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody RegisterCustomerRequest request,
            UriComponentsBuilder uriBuilder) {

        int lastNameCountThreshold = customerConfig.getThreshold();
        if (customerRepository.countByLastName(request.getLastName()) == lastNameCountThreshold) {
            System.out.println("too many customers");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Same last name should not appear more than " + lastNameCountThreshold + " times");
        }

        Customer customer = customerMapper.toEntity(request);
        // Hash password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        URI uri = uriBuilder.path("/api/customers/{id}").buildAndExpand(customer.getId()).toUri();

        // TODO EXPLAIN: return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDto(customer));
        // return location, good rest practice
        return ResponseEntity.created(uri).body(customerMapper.toDto(customer));
    }

    @PutMapping("/{customer_id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable(name = "customer_id") UUID id,
            @RequestBody UpdateCustomerRequest request
    ) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        customerMapper.update(request, customer);
        customerRepository.save(customer);

        return ResponseEntity.ok(customerMapper.toDto(customer));
    }

    // TODO EXPLAIN: 1. happy case 204 no content 2. non-exist customer, 404
    @DeleteMapping("/{customer_id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable(name = "customer_id") UUID id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        customerRepository.delete(customer);

        return ResponseEntity.noContent().build();
    }

    // TODO EXPLAIN: 1. happy case 204 no content 2. 401 unauthorized 3.non-exist customer, 404
    // Action-based updates
    @PostMapping("/{customer_id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable(name = "customer_id") UUID id,
            @RequestBody ChangePasswordRequest request) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        String hashedOldPasswordFromRequest = passwordEncoder.encode(request.getOldPassword());

        System.out.println("old password OG: " + request.getOldPassword());
        System.out.println("old password: " + hashedOldPasswordFromRequest);
        System.out.println("db password: " + customer.getPassword());

        // With BCrypt (Spring Security's default), hashing the same password twice produces different hashes
        // (due to the random salt), so equals() will almost never match.
        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            // TODO EXPLAIN: 400 Bad Request vs UNAUTHORIZED
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO EXPLAIN: no need to user mapper, which is for large/complex objects
        // Encode the new password and save
        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);

        return ResponseEntity.noContent().build();
    }

    // TODO EXPLAIN: local
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
