package com.interview.service;

import com.interview.dto.CustomerDTO;
import com.interview.entity.Customer;
import com.interview.exception.ResourceConflictException;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServiceAppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    /**
     * Gets a customer by ID.
     *
     * @param id the customer ID
     * @return the customer DTO
     * @throws NoSuchElementException if the customer is not found
     */
    public CustomerDTO getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found with id " + id));
        return customerMapper.toDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::toDTO).collect(Collectors.toList());
    }


    /**
     * Updates a customer by ID.
     *
     * @param id          the customer ID
     * @param customerDetails the customer DTO
     * @return the updated customer entity
     * @throws NoSuchElementException if the customer is not found
     */
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDetails) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found with id " + id));
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        return customerMapper.toDTO(customerRepository.save(customer));
    }


    /**
     * Deletes a customer by ID.
     *
     * @param id the customer ID
     * @throws NoSuchElementException if the customer is not found
     */
    public void deleteCustomer(Long id) {
        try {
            if (serviceAppointmentRepository.existsByCustomerId(id)) {
                throw new ResourceConflictException("Cannot delete customer as it is referenced by an appointment.");
            }
            logger.info("Deleting customer with ID: {}", id);
            customerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            logger.error("Delete failed for: " + id);
            throw new NoSuchElementException("Customer not found with id " + id);
        }
    }
}
