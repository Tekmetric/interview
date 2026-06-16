package com.interview.application.customer;

import com.interview.application.CustomerRepository;
import com.interview.domain.Customer;

import java.util.UUID;

public class CreateCustomer {

    private final CustomerRepository repository;

    public CreateCustomer(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer execute(String name, String email) {
        Customer customer = new Customer(UUID.randomUUID(), name, email);
        return repository.save(customer);
    }
}
