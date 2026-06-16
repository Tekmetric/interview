package com.interview.application.customer;

import com.interview.application.CustomerRepository;
import com.interview.domain.Customer;

import java.util.List;

public class ListCustomers {

    private final CustomerRepository repository;

    public ListCustomers(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> execute() {
        return repository.findAll();
    }
}
