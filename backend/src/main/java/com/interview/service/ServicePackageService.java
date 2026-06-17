package com.interview.service;

import com.interview.dto.CustomerResponse;
import com.interview.dto.ServicePackageRequest;
import com.interview.dto.ServicePackageResponse;
import com.interview.dto.SubscriberDto;
import com.interview.dto.SubscribersResponse;
import com.interview.entity.Customer;
import com.interview.entity.ServicePackage;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.ServicePackageNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.mapper.ServicePackageMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServicePackageRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing service package operations including CRUD and subscription management.
 *
 * <p>This service handles the business logic for service package management, including:
 * <ul>
 *   <li>Creating and updating service packages</li>
 *   <li>Retrieving packages by ID, listing all packages with filtering</li>
 *   <li>Soft delete operations (activate/deactivate)</li>
 *   <li>Customer subscription management</li>
 * </ul>
 *
 * <p>All read operations are performed within read-only transactions for optimal performance.
 * Write operations use full transactions with proper exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;
    private final CustomerRepository customerRepository;
    private final ServicePackageMapper servicePackageMapper;
    private final CustomerMapper customerMapper;

    /**
     * Create a new service package.
     */
    @Transactional
    public ServicePackageResponse createServicePackage(ServicePackageRequest request) {
        log.debug("Creating service package with name: {}", request.name());

        // Check if package with name already exists (including inactive ones)
        if (servicePackageRepository.existsByName(request.name())) {
            throw new BadRequestException("Service package with name '" + request.name() + "' already exists");
        }

        // Create package entity
        ServicePackage servicePackage = servicePackageMapper.toEntity(request);

        ServicePackage savedPackage = servicePackageRepository.save(servicePackage);

        log.info("Created service package with ID: {} and name: {}", savedPackage.getId(), savedPackage.getName());
        return servicePackageMapper.toResponseWithoutSubscribers(savedPackage);
    }

    /**
     * Get service package by ID (includes subscriber data).
     */
    public ServicePackageResponse getServicePackageById(Long id) {
        log.debug("Fetching service package with ID: {}", id);

        ServicePackage servicePackage = servicePackageRepository.findByIdWithSubscribers(id)
            .orElseThrow(() -> new ServicePackageNotFoundException(id));

        return servicePackageMapper.toResponse(servicePackage);
    }

    /**
     * Update service package.
     */
    @Transactional
    public ServicePackageResponse updateServicePackage(Long id, ServicePackageRequest request) {
        log.debug("Updating service package with ID: {}", id);

        ServicePackage existingPackage = servicePackageRepository.findById(id)
            .orElseThrow(() -> new ServicePackageNotFoundException(id));

        // Check if name is changing and new name already exists
        if (!existingPackage.getName().equals(request.name())
            && servicePackageRepository.existsByName(request.name())) {
            throw new BadRequestException("Service package with name '" + request.name() + "' already exists");
        }

        // Update package fields
        servicePackageMapper.updateEntity(existingPackage, request);

        ServicePackage updatedPackage = servicePackageRepository.save(existingPackage);

        log.info("Updated service package with ID: {} and name: {}", updatedPackage.getId(), updatedPackage.getName());
        return servicePackageMapper.toResponseWithoutSubscribers(updatedPackage);
    }

    /**
     * Get all service packages with optional active filter.
     */
    public List<ServicePackageResponse> getAllServicePackages(Boolean active) {
        log.debug("Fetching all service packages with active filter: {}", active);

        List<ServicePackage> packages;
        if (Boolean.TRUE.equals(active)) {
            packages = servicePackageRepository.findAllActiveWithSubscribers();
        } else if (Boolean.FALSE.equals(active)) {
            // Get inactive packages only
            packages = servicePackageRepository.findAllWithSubscribers().stream()
                .filter(pkg -> !pkg.isActive())
                .toList();
        } else {
            // Get all packages (active + inactive)
            packages = servicePackageRepository.findAllWithSubscribers();
        }

        return servicePackageMapper.toResponseList(packages);
    }

    /**
     * Get service packages with pagination and optional active filter.
     */
    public Page<ServicePackageResponse> getServicePackagesWithPagination(Boolean active, Pageable pageable) {
        log.debug("Fetching service packages with pagination: {}, active filter: {}", pageable, active);

        Page<ServicePackage> packagePage;
        if (Boolean.TRUE.equals(active)) {
            packagePage = servicePackageRepository.findAllActiveWithSubscribers(pageable);
        } else if (Boolean.FALSE.equals(active)) {
            // Get inactive packages only
            packagePage = servicePackageRepository.findAllInactiveWithSubscribers(pageable);
        } else {
            // Get all packages (active + inactive)
            packagePage = servicePackageRepository.findAllWithSubscribers(pageable);
        }

        return packagePage.map(servicePackageMapper::toResponse);
    }

    /**
     * Activate or deactivate a service package (soft delete).
     */
    @Transactional
    public ServicePackageResponse updateServicePackageStatus(Long id, boolean active) {
        log.debug("Updating service package {} status to: {}", id, active);

        ServicePackage servicePackage = servicePackageRepository.findByIdWithSubscribers(id)
            .orElseThrow(() -> new ServicePackageNotFoundException(id));

        if (servicePackage.isActive() == active) {
            log.warn("Service package {} is already {}", id, active ? "active" : "inactive");
            return servicePackageMapper.toResponse(servicePackage);
        }

        if (active) {
            servicePackage.activate();
            log.info("Activated service package with ID: {}", id);
        } else {
            servicePackage.deactivate();
            log.info("Deactivated service package with ID: {}", id);
        }

        ServicePackage updatedPackage = servicePackageRepository.save(servicePackage);
        return servicePackageMapper.toResponse(updatedPackage);
    }

    /**
     * Subscribe a customer to a service package.
     */
    @Transactional
    public void subscribeCustomerToPackage(Long servicePackageId, Long customerId) {
        log.debug("Subscribing customer {} to service package {}", customerId, servicePackageId);

        // Load entities with their collections
        Customer customer = customerRepository.findByIdWithSubscriptions(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        ServicePackage servicePackage = servicePackageRepository.findByIdWithSubscribers(servicePackageId)
            .orElseThrow(() -> new ServicePackageNotFoundException(servicePackageId));

        // Check if already subscribed
        if (customer.getSubscribedPackages().contains(servicePackage)) {
            throw new BadRequestException("Customer is already subscribed to this service package");
        }

        // Use bidirectional synchronization helper method
        customer.addServicePackage(servicePackage);

        // Save from the owning side (Customer)
        customerRepository.save(customer);

        log.info("Successfully subscribed customer {} to service package {}",
            customerId, servicePackageId);
    }

    /**
     * Unsubscribe a customer from a service package.
     */
    @Transactional
    public void unsubscribeCustomerFromPackage(Long servicePackageId, Long customerId) {
        log.debug("Unsubscribing customer {} from service package {}", customerId, servicePackageId);

        Customer customer = customerRepository.findByIdWithSubscriptions(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        ServicePackage servicePackage = servicePackageRepository.findByIdWithSubscribers(servicePackageId)
            .orElseThrow(() -> new ServicePackageNotFoundException(servicePackageId));

        // Check if actually subscribed
        if (!customer.getSubscribedPackages().contains(servicePackage)) {
            throw new BadRequestException("Customer is not subscribed to this service package");
        }

        // Use bidirectional synchronization helper method
        customer.removeServicePackage(servicePackage);

        // Save from the owning side (Customer)
        customerRepository.save(customer);

        log.info("Successfully unsubscribed customer {} from service package {}",
            customerId, servicePackageId);
    }

    /**
     * Get all subscribers of a service package.
     */
    public SubscribersResponse getServicePackageSubscribers(Long servicePackageId) {
        log.debug("Fetching subscribers for service package {}", servicePackageId);

        ServicePackage servicePackage = servicePackageRepository.findByIdWithSubscribers(servicePackageId)
            .orElseThrow(() -> new ServicePackageNotFoundException(servicePackageId));

        List<SubscriberDto> subscribers = servicePackage.getSubscribers().stream()
            .map(customer -> SubscriberDto.of(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
            ))
            .toList();

        log.debug("Found {} subscribers for service package {}", subscribers.size(), servicePackageId);

        return SubscribersResponse.of(subscribers);
    }
}