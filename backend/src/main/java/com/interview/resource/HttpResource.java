package com.interview.resource;

import com.interview.model.CustomerDTO;
import com.interview.service.CustomerService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class HttpResource {

    private final CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(HttpResource.class);

    public HttpResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }

    @Operation(summary = "List all customers in data store.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully listed all customers")})
    @GetMapping("/customers/all")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Paginated endpoint for retrieving customers")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully listed all customers")})
    @GetMapping("/customers")
    public Page<CustomerDTO> getCustomers(@RequestParam(defaultValue = "0", name = "page") int page,
                                       @RequestParam(defaultValue = "10", name = "size") int size) {
        return customerService.getCustomers(page, size);
    }

    @Operation(summary = "Retrieve a customer by ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated customer record"), @ApiResponse(code = 404, message = "Customer was not found.")})
    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        Optional<CustomerDTO> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new customer record.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Successfully created customer record"), @ApiResponse(code = 400, message = "Bad request.")})
    @PostMapping("/customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            return new ResponseEntity<>(customerService.saveCustomer(customerDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Could not add customer", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update a customer record by ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated customer record"), @ApiResponse(code = 404, message = "Customer was not found.")})
    @PutMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {             //including duplicate key - DataIntegrityViolationException
            logger.error("Cannot update customer due to bad request {}", customerDTO, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete a customer by Id.")
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}