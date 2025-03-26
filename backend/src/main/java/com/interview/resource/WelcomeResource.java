package com.interview.resource;

import com.interview.model.CustomerDTO;
import com.interview.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/customer")
    public CustomerDTO createCustomer( @RequestBody CustomerDTO customerDTO) {
        return customerService.saveCustomer(customerDTO);
    }
}