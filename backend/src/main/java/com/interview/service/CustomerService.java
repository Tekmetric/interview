package com.interview.service;

import com.interview.model.CustomerDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();
    Optional<CustomerDTO> getCustomerById(Long id);
    CustomerDTO saveCustomer(CustomerDTO employeeDTO);
    CustomerDTO updateCustomer(Long id, CustomerDTO employeeDTO);
    void deleteCustomer(Long id);
}
