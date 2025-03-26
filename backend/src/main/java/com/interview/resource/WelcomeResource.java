package com.interview.resource;

import com.interview.model.CustomerDTO;
import com.interview.service.CustomerService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.Optional;

@RestController
public class WelcomeResource {

    private final CustomerService customerService;

    public WelcomeResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }

    @Operation(summary = "List all customers in data store.")
    @ApiResponses(value = { @ApiResponse( code = 200, message = "Successfully listed all customers")})
    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Retrieve a customer by ID.")
    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> getEmployeeById(@PathVariable Long id) {
        Optional<CustomerDTO> employee = customerService.getCustomerById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new customer record.")
    @PostMapping("/customer")
    public CustomerDTO createCustomer( @RequestBody CustomerDTO customerDTO) {
        return customerService.saveCustomer(customerDTO);
    }

    @Operation(summary = "Update a customer record by ID.")
    @PutMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> updateEmployee(@PathVariable Long id, @RequestBody CustomerDTO employeeDTO) {
        try {
            CustomerDTO updatedEmployee = customerService.updateCustomer(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a customer by Id.")
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}