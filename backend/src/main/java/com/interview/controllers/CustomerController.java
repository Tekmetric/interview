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

//    // TODO add api in path?
//    // TODO EXPLAIN: GetMapping vs RequestMapping
//    @GetMapping
//    // TODO EXPLAIN: Iterable not List
//    // TODO EXPLAIN: required = false, defaultValue = "", name = "sort"
//    @Cacheable(value = "customers", key = "#sort", unless = "#result.isEmpty()")
//    public Iterable<CustomerDto> getAllCustomers(@RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
//        // Map of allowed sort keys (case-insensitive) to actual entity field names
//        Map<String, String> sortMapping = Map.of(
//                "email", "email",
//                "lastname", "lastName"
//        );
//
//        String normalizedSort = sortMapping.getOrDefault(sort.toLowerCase(), "lastName");
//
//        // TODO EXPLAIN: lambda -> method reference
//        // customer -> customerMapper.toDto(customer)
//        return customerRepository.findAll(Sort.by(normalizedSort)).stream().map(customerMapper::toDto).toList();
//    }

//    @GetMapping
//    @Cacheable(value = "customers", key = "#sort + '-' + #page + '-' + #size", unless = "#result.isEmpty()")
//    public Page<CustomerDto> getAllCustomers(
//            @RequestParam(required = false, defaultValue = "", name = "sort") String sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size) {
//
//        // Map of allowed sort keys (case-insensitive) to actual entity field names
//        Map<String, String> sortMapping = Map.of(
//                "email", "email",
//                "lastname", "lastName"
//        );
//
//        String normalizedSort = sortMapping.getOrDefault(sort.toLowerCase(), "lastName");
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(normalizedSort));
//
//        return customerRepository.findAll(pageable).map(customerMapper::toDto);
//    }

//    @GetMapping
//    public PagedModel<EntityModel<CustomerDto>> getAllCustomers(
//            @RequestParam(required = false, defaultValue = "", name = "sort") String sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size,
//            PagedResourcesAssembler<CustomerDto> assembler) {
//
//        Page<CustomerDto> dtoPage = customerService.getCustomers(sort, page, size);
//        return assembler.toModel(dtoPage);
//    }

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

        if (!customer.getPassword().equals(request.getOldPassword())) {
            // TODO EXPLAIN: 400 Bad Request vs UNAUTHORIZED
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO EXPLAIN: no need to user mapper, which is for large/complex objects
        customer.setPassword(request.getNewPassword());
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
