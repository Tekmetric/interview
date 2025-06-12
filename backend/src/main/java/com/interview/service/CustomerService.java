package com.interview.service;

import com.interview.dto.CustomerRequestDTO;
import com.interview.dto.CustomerResponseDTO;
import com.interview.dto.CustomerSummaryDTO;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.model.CustomerEntity;
import com.interview.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing customers. Provides methods to create, read, update, and delete
 * customers, as well as methods to fetch customer summaries and details.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Transactional(readOnly = true)
  public List<CustomerSummaryDTO> getAllCustomers() {
    log.debug("Fetching all customers");
    List<CustomerEntity> customers = customerRepository.findAll();
    return customerMapper.toSummaryDTOList(customers);
  }

  @Transactional(readOnly = true)
  public Page<CustomerSummaryDTO> getAllCustomers(Pageable pageable) {
    log.debug("Fetching customers with pagination: {}", pageable);
    Page<CustomerEntity> customers = customerRepository.findAll(pageable);
    return customers.map(customerMapper::toSummaryDTO);
  }

  @Transactional(readOnly = true)
  public CustomerResponseDTO getCustomerById(Long id) {
    log.debug("Fetching customer with id: {}", id);
    CustomerEntity customer = customerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    return customerMapper.toResponseDTO(customer);
  }

  @Transactional(readOnly = true)
  public CustomerResponseDTO getCustomerByIdWithVehicles(Long id) {
    log.debug("Fetching customer with vehicles for id: {}", id);
    CustomerEntity customer = customerRepository.findByIdWithVehicles(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    return customerMapper.toResponseDTOWithVehicles(customer);
  }

  @Transactional(readOnly = true)
  public CustomerResponseDTO getCustomerByEmail(String email) {
    log.debug("Fetching customer with email: {}", email);
    CustomerEntity customer = customerRepository.findByEmail(email)
        .orElseThrow(
            () -> new ResourceNotFoundException("Customer not found with email: " + email));
    return customerMapper.toResponseDTO(customer);
  }

  public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequest) {
    log.debug("Creating new customer: {}", customerRequest.email());

    if (customerRepository.existsByEmail(customerRequest.email())) {
      throw new ResourceAlreadyExistsException(
          "Customer already exists with email: " + customerRequest.email());
    }

    CustomerEntity customer = customerMapper.toEntity(customerRequest);
    CustomerEntity savedCustomer = customerRepository.save(customer);
    log.info("Customer created successfully with id: {}", savedCustomer.getId());
    return customerMapper.toResponseDTO(savedCustomer);
  }

  public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequest) {
    log.debug("Updating customer with id: {}", id);

    CustomerEntity existingCustomer = customerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

    // Check if email is being changed and if new email already exists
    if (!existingCustomer.getEmail().equals(customerRequest.email()) &&
        customerRepository.existsByEmail(customerRequest.email())) {
      throw new ResourceAlreadyExistsException(
          "Customer already exists with email: " + customerRequest.email());
    }

    // Update fields using mapper
    customerMapper.updateEntityFromDTO(existingCustomer, customerRequest);

    CustomerEntity updatedCustomer = customerRepository.save(existingCustomer);
    log.info("Customer updated successfully with id: {}", updatedCustomer.getId());
    return customerMapper.toResponseDTO(updatedCustomer);
  }

  public void deleteCustomer(Long id) {
    log.debug("Deleting customer with id: {}", id);

    CustomerEntity customer = customerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    customerRepository.delete(customer);
    log.info("Customer deleted successfully with id: {}", id);
  }

  @Transactional(readOnly = true)
  public long getTotalCustomers() {
    return customerRepository.count();
  }

}
