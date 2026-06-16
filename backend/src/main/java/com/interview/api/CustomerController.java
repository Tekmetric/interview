package com.interview.api;

import com.interview.api.dto.CustomerRequest;
import com.interview.api.dto.CustomerResponse;
import com.interview.application.customer.CreateCustomer;
import com.interview.application.customer.DeleteCustomer;
import com.interview.application.customer.GetCustomer;
import com.interview.application.customer.ListCustomers;
import com.interview.application.customer.UpdateCustomer;
import com.interview.domain.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@Transactional
public class CustomerController {

    private final CreateCustomer createCustomer;
    private final GetCustomer getCustomer;
    private final UpdateCustomer updateCustomer;
    private final ListCustomers listCustomers;
    private final DeleteCustomer deleteCustomer;

    public CustomerController(CreateCustomer createCustomer, GetCustomer getCustomer,
                              UpdateCustomer updateCustomer, ListCustomers listCustomers,
                              DeleteCustomer deleteCustomer) {
        this.createCustomer = createCustomer;
        this.getCustomer = getCustomer;
        this.updateCustomer = updateCustomer;
        this.listCustomers = listCustomers;
        this.deleteCustomer = deleteCustomer;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = createCustomer.execute(request.getName(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(customer));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        Customer customer = getCustomer.execute(id);
        return ResponseEntity.ok(toResponse(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody CustomerRequest request) {
        Customer customer = updateCustomer.execute(id, request.getName(), request.getEmail());
        return ResponseEntity.ok(toResponse(customer));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<CustomerResponse>> list() {
        List<CustomerResponse> list = listCustomers.execute().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCustomer.execute(id);
        return ResponseEntity.noContent().build();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
