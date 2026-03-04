package com.interview.customer.service;

import com.interview.common.error.ResourceNotFoundException;
import com.interview.customer.dao.CustomerRepository;
import com.interview.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findByIdOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class, id));
    }
}
