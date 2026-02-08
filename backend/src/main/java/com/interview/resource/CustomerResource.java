package com.interview.resource;

import com.interview.dto.CustomerDTO;
import com.interview.dto.ServiceJobDTO;
import com.interview.dto.VehicleDTO;
import com.interview.service.CustomerService;
import com.interview.service.ServiceJobService;
import com.interview.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final CustomerService customerService;
    private final VehicleService vehicleService;
    private final ServiceJobService serviceJobService;

    public CustomerResource(CustomerService customerService, VehicleService vehicleService, ServiceJobService serviceJobService) {
        this.customerService = customerService;
        this.vehicleService = vehicleService;
        this.serviceJobService = serviceJobService;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        if (customerDTO.getId() != null) {
            throw new IllegalArgumentException("A new customer cannot already have an ID");
        }
        CustomerDTO result = customerService.create(customerDTO);
        return ResponseEntity.created(new URI("/api/customers/" + result.getId())).body(result);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
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
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(Pageable pageable) {
        Page<CustomerDTO> page = customerService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customerDTO = customerService.findOne(id);
        return ResponseEntity.ok(customerDTO);
    }

    @GetMapping("/customers/{id}/vehicles")
    public ResponseEntity<Page<VehicleDTO>> getVehiclesForCustomer(@PathVariable Long id, Pageable pageable) {
        Page<VehicleDTO> page = vehicleService.findByCustomerId(id, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/customers/{id}/service-jobs")
    public ResponseEntity<Page<ServiceJobDTO>> getServiceJobsForCustomer(@PathVariable Long id, Pageable pageable) {
        Page<ServiceJobDTO> page = serviceJobService.findByCustomerId(id, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
