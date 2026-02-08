package com.interview.service;

import com.interview.dto.CustomerDTO;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Create a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerDTO create(CustomerDTO customerDTO) {
        com.interview.model.Customer customer = CustomerMapper.toEntity(customerDTO);
        customer = customerRepository.save(customer);
        return CustomerMapper.toDto(customer);
    }

    /**
     * Update a customer.
     *
     * @param customerDTO the entity to update.
     * @return the persisted entity.
     */
    public CustomerDTO update(CustomerDTO customerDTO) {
        return customerRepository.findById(customerDTO.getId())
            .map(existingCustomer -> {
                existingCustomer.setFirstName(customerDTO.getFirstName());
                existingCustomer.setLastName(customerDTO.getLastName());
                existingCustomer.setEmail(customerDTO.getEmail());
                return customerRepository.save(existingCustomer);
            })
            .map(CustomerMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found!"));
    }

    /**
     * Partially updates a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public CustomerDTO partialUpdate(CustomerDTO customerDTO) {
        return customerRepository.findById(customerDTO.getId())
            .map(existingCustomer -> {
                if (customerDTO.getFirstName() != null) {
                    existingCustomer.setFirstName(customerDTO.getFirstName());
                }
                if (customerDTO.getLastName() != null) {
                    existingCustomer.setLastName(customerDTO.getLastName());
                }
                if (customerDTO.getEmail() != null) {
                    existingCustomer.setEmail(customerDTO.getEmail());
                }
                return customerRepository.save(existingCustomer);
            })
            .map(CustomerMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found!"));
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable)
            .map(CustomerMapper::toDto);
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public CustomerDTO findOne(Long id) {
        return customerRepository.findById(id)
            .map(CustomerMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found!"));
    }


    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
