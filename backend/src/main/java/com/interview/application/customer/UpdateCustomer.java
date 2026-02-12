package com.interview.application.customer;

import com.interview.application.CustomerRepository;
import com.interview.application.EntityNotFoundException;
import com.interview.domain.Customer;

import java.util.UUID;

public class UpdateCustomer {

    private final CustomerRepository repository;

    public UpdateCustomer(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer execute(UUID id, String name, String email) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        Customer updated = new Customer(existing.getId(), name, email);
        return repository.save(updated);
    }
}
