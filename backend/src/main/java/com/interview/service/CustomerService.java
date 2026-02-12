package com.interview.service;

import com.interview.dto.CustomerRequest;
import com.interview.dto.CustomerResponse;
import com.interview.entity.Customer;
import com.interview.entity.CustomerProfile;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerAlreadyExistsException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.OptimisticLockingException;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing customer operations including CRUD operations and customer profiles.
 *
 * <p>This service handles the business logic for customer management, including:
 * <ul>
 *   <li>Creating customers with optional profile data</li>
 *   <li>Retrieving customers by ID, listing all customers or paginated list of customers</li>
 *   <li>Updating customer information and profiles</li>
 *   <li>Deleting customers (cascades to profiles)</li>
 * </ul>
 *
 * <p>All read operations are performed within read-only transactions for optimal performance.
 * Write operations use full transactions with proper exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    public static final String CUSTOMER = "Customer";
    public static final String VERSION_IS_REQUIRED = "Version is required for updates. Please include the current version from GET response.";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Create a new customer with profile.
     */
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.debug("Creating customer with email: {}", request.email());

        // Check if customer with email already exists
        if (customerRepository.existsByEmail(request.email())) {
            throw new CustomerAlreadyExistsException(request.email());
        }

        // Create customer entity
        Customer customer = customerMapper.toEntity(request);

        // Create profile if profile fields are provided
        if (hasProfileData(request)) {
            CustomerProfile profile = customerMapper.toProfileEntity(request);
            profile.setCustomer(customer);
            customer.setCustomerProfile(profile);
        }

        Customer savedCustomer = customerRepository.save(customer);

        log.info("Created customer with ID: {} and email: {}", savedCustomer.getId(), savedCustomer.getEmail());
        return customerMapper.toResponse(savedCustomer);
    }

    /**
     * Get customer by ID (always includes profile data).
     */
    public CustomerResponse getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findByIdWithProfile(id).orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    /**
     * Get all customers (always includes profile data).
     */
    public List<CustomerResponse> getAllCustomers() {
        log.debug("Fetching all customers");

        List<Customer> customers = customerRepository.findAllWithProfiles();
        return customerMapper.toResponseList(customers);
    }

    /**
     * Get customers with pagination (always includes profile data).
     */
    public Page<CustomerResponse> getCustomersWithPagination(Pageable pageable) {
        log.debug("Fetching customers with pagination: {}", pageable);

        Page<Customer> customerPage = customerRepository.findAllWithProfiles(pageable);
        return customerPage.map(customerMapper::toResponse);
    }

    /**
     * Update customer and profile.
     */
    @Transactional
    public void updateCustomer(Long customerId, CustomerRequest request) {
        log.debug("Updating customer with ID: {} with version: {}", customerId, request.version());

        validateUpdateRequest(request);

        Customer existingCustomer = customerRepository.findByIdWithProfile(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        validateOptimisticLocking(customerId, request.version(), existingCustomer.getVersion());
        validateEmailUniqueness(request.email(), existingCustomer.getEmail());

        // Update customer fields
        customerMapper.updateEntity(existingCustomer, request);

        // Update or create profile
        if (hasProfileData(request)) {
            if (existingCustomer.getCustomerProfile() != null) {
                // Update existing profile
                customerMapper.updateProfileEntity(existingCustomer.getCustomerProfile(), request);
            } else {
                // Create new profile
                CustomerProfile profile = customerMapper.toProfileEntity(request);
                profile.setCustomer(existingCustomer);
                existingCustomer.setCustomerProfile(profile);
            }
        }

        try {
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            log.info("Updated customer with ID: {} to version: {}", updatedCustomer.getId(), updatedCustomer.getVersion());
        } catch (jakarta.persistence.OptimisticLockException | org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
            log.warn("Optimistic locking failure during save for customer ID: {}", customerId, ex);
            throw new OptimisticLockingException(CUSTOMER, customerId);
        }
    }

    /**
     * Delete customer (profile will be deleted automatically due to cascade).
     */
    @Transactional
    public void deleteCustomer(Long id) {
        log.debug("Deleting customer with ID: {}", id);

        int deletedCount = customerRepository.deleteByCustomerId(id);

        if (deletedCount == 0) {
            throw new CustomerNotFoundException(id);
        }

        log.info("Deleted customer with ID: {}", id);
    }

    /**
     * Validate version on update request.
     */
    private void validateUpdateRequest(CustomerRequest request) {
        if (request.version() == null) {
            throw new BadRequestException(VERSION_IS_REQUIRED);
        }
    }

    /**
     * Validate optimistic locking version.
     */
    private void validateOptimisticLocking(Long customerId, Long requestVersion, Long currentVersion) {
        if (!requestVersion.equals(currentVersion)) {
            log.warn("Optimistic locking conflict for customer ID: {}. Expected version: {}, but current version: {}",
                customerId, requestVersion, currentVersion);
            throw new OptimisticLockingException(CUSTOMER, customerId);
        }
    }

    /**
     * Validate email uniqueness when email is being changed.
     */
    private void validateEmailUniqueness(String newEmail, String currentEmail) {
        if (!currentEmail.equals(newEmail) && customerRepository.existsByEmail(newEmail)) {
            throw new CustomerAlreadyExistsException(newEmail);
        }
    }

    /**
     * Check if request contains any profile data.
     */
    private boolean hasProfileData(CustomerRequest request) {
        return request.address() != null || request.dateOfBirth() != null || request.preferredContactMethod() != null;
    }
}