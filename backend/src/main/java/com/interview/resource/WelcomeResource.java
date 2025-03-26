package com.interview.resource;

import com.interview.model.CustomerDTO;
import com.interview.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class WelcomeResource {

    private final CustomerService customerService;

    public WelcomeResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping("/api/welcome")
    public String index() {

        return "Welcome to the interview project!";
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> getEmployeeById(@PathVariable Long id) {
        Optional<CustomerDTO> employee = customerService.getCustomerById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/customer")
    public CustomerDTO createCustomer( @RequestBody CustomerDTO customerDTO) {
        return customerService.saveCustomer(customerDTO);
    }

    @PutMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> updateEmployee(@PathVariable Long id, @RequestBody CustomerDTO employeeDTO) {
        try {
            CustomerDTO updatedEmployee = customerService.updateCustomer(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}