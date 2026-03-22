package com.interview.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interview.config.CacheNames;
import com.interview.persistence.entity.Customer;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DealershipException;
import com.interview.exception.DuplicateResourceException;
import com.interview.mapper.CustomerMapper;
import com.interview.mapper.EntityMapper;
import com.interview.dto.request.CreateCustomerRequest;
import com.interview.dto.request.UpdateCustomerRequest;
import com.interview.dto.response.CustomerResponse;
import com.interview.repository.CustomerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService extends AbstractCrudService<Customer, CustomerResponse, CreateCustomerRequest, UpdateCustomerRequest> {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    protected JpaRepository<Customer, UUID> getRepository() {
        return customerRepository;
    }

    @Override
    protected EntityMapper<Customer, CustomerResponse, CreateCustomerRequest, UpdateCustomerRequest> getMapper() {
        return customerMapper;
    }

    @Override
    protected DealershipException notFoundException(final UUID id) {
        return new CustomerNotFoundException(id);
    }

    @Override
    @Transactional
    public CustomerResponse create(final CreateCustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());
        try {
            Customer customer = customerMapper.toEntity(request);
            return customerMapper.toResponse(customerRepository.save(customer));
        } catch (final DataIntegrityViolationException ex) {
            throw new DuplicateResourceException(
                    "A customer with email '" + request.getEmail() + "' already exists", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CUSTOMERS, key = "#id")
    public CustomerResponse findById(final UUID id) {
        log.debug("Fetching customer: {}", id);
        return super.findById(id);
    }

    @Override
    @Transactional
    @CachePut(value = CacheNames.CUSTOMERS, key = "#id")
    public CustomerResponse update(final UUID id, final UpdateCustomerRequest request) {
        log.info("Updating customer: {}", id);
        final Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMapper.updateEntity(request, customer);

        try {
            return customerMapper.toResponse(customerRepository.save(customer));
        } catch (final DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("A customer with that email already exists", ex);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CUSTOMERS, key = "#id")
    public void delete(final UUID id) {
        log.info("Deleting customer: {}", id);
        super.delete(id);
    }
}
