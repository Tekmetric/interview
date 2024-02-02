package com.interview.service;

import com.interview.service.model.Customer;
import com.interview.service.model.CustomerIdentification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerPersistence {

    public Page<CustomerIdentification> findAllByPage(Pageable pageable);
    public Optional<Customer> findById(Long id);
    public Customer saveCustomer(Customer customer);
    public void deleteCustomer(Long id);
    public Customer updateCustomer(Customer customer);


}
