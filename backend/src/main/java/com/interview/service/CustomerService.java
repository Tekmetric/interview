package com.interview.service;

import com.interview.model.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();
    Page<CustomerDTO> getCustomers(int page, int size, String sortBy, String sortDirection);
    Optional<CustomerDTO> getCustomerById(Long id);
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
}
