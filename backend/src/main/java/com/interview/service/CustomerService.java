package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.service.model.Customer;
import com.interview.service.model.CustomerIdentification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class CustomerService {

    private CustomerPersistence persistence;


    @Transactional(readOnly = true)
    public Page<CustomerIdentification> findAllByPage(Pageable pageable) {
        return persistence.findAllByPage(pageable);
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(Long id) {
        return persistence.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException(String.format("Customer with id %s was not found", id))
        );
    }

    @Transactional
    public Customer addCustomer(Customer customer) {
        return persistence.saveCustomer(customer);
    }

    @Transactional
    public void removeCustomer(Long id) {
        persistence.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Customer with id %s was not found", id))
        );
        persistence.deleteCustomer(id);
    }

    @Transactional
    public Customer updateCustomer(Customer customer){
        persistence.findById(customer.getId()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Customer with id %s was not found", customer.getId()))
        );
        return persistence.updateCustomer(customer);
    }

}

