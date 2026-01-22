package com.interview.resource;

import com.interview.dto.CustomerDTO;
import com.interview.service.CustomerService;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final CustomerService customerService;

    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        if (customerDTO.getId() != null) {
            throw new IllegalArgumentException("A new customer cannot already have an ID");
        }
        CustomerDTO result = customerService.create(customerDTO);
        return ResponseEntity.created(new URI("/api/customers/" + result.getId())).body(result);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        customerDTO.setId(id);
        CustomerDTO result = customerService.update(customerDTO);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> partialUpdateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        customerDTO.setId(id);
        CustomerDTO result = customerService.partialUpdate(customerDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> list = customerService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customerDTO = customerService.findOne(id);
        return ResponseEntity.ok(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
