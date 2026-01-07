package com.interview.service;

import com.interview.dto.CustomerDTO;
import com.interview.entity.CustomerEntity;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(String email, String firstName, String lastName, Pageable pageable) {
        log.info("Fetching customers with pagination - page: {}, size: {}, email: {}, firstName: {}, lastName: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), email, firstName, lastName);
        
        if (email != null && !email.isBlank()) {
            return customerRepository.findByEmailContainingIgnoreCase(email, pageable)
                    .map(CustomerMapper::toDTO);
        }
        
        if (firstName != null && !firstName.isBlank()) {
            return customerRepository.findByFirstNameContainingIgnoreCase(firstName, pageable)
                    .map(CustomerMapper::toDTO);
        }
        
        if (lastName != null && !lastName.isBlank()) {
            return customerRepository.findByLastNameContainingIgnoreCase(lastName, pageable)
                    .map(CustomerMapper::toDTO);
        }
        
        return customerRepository.findAll(pageable)
                .map(CustomerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        log.info("Fetching customer with id: {}", id);
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer with email: {}", customerDTO.getEmail());
        
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        
        CustomerEntity customer = CustomerMapper.toEntity(customerDTO);
        CustomerEntity savedCustomer = customerRepository.save(customer);
        
        log.info("Customer created successfully with id: {}", savedCustomer.getId());
        return CustomerMapper.toDTO(savedCustomer);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer with id: {}", id);
        
        CustomerEntity existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) 
                && customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        
        CustomerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
        CustomerEntity updatedCustomer = customerRepository.save(existingCustomer);
        
        log.info("Customer updated successfully with id: {}", id);
        return CustomerMapper.toDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with id: {}", id);
    }
}
