package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.filter.VehicleFilter;
import com.interview.entity.Customer;
import com.interview.entity.Vehicle;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.VehicleNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import com.interview.specification.VehicleSpecs;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing vehicle operations including CRUD operations.
 *
 * <p>This service handles the business logic for vehicle management, including:
 * <ul>
 *   <li>Creating vehicles with customer validation</li>
 *   <li>Retrieving vehicles by ID, listing all vehicles or paginated list of vehicles</li>
 *   <li>Updating vehicle information</li>
 *   <li>Deleting vehicles</li>
 * </ul>
 *
 * <p>All read operations are performed within read-only transactions for optimal performance.
 * Write operations use full transactions with proper exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;

    /**
     * Create a new vehicle for a customer.
     */
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        log.debug("Creating vehicle with VIN: {} for customer ID: {}", request.vin(), request.customerId());

        // Check if VIN already exists
        if (vehicleRepository.existsByVin(request.vin())) {
            throw new BadRequestException("Vehicle with VIN " + request.vin() + " already exists");
        }

        // Validate customer exists
        if (!customerRepository.existsById(request.customerId())) {
            throw new CustomerNotFoundException(request.customerId());
        }

        // Create vehicle entity
        Vehicle vehicle = vehicleMapper.toEntity(request);
        Customer customerRef = customerRepository.getReferenceById(request.customerId());
        vehicle.setCustomer(customerRef);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        log.info("Created vehicle with ID: {} and VIN: {} for customer ID: {}", savedVehicle.getId(), savedVehicle.getVin(), request.customerId());

        return vehicleMapper.toCreateResponse(savedVehicle);
    }

    /**
     * Get vehicle by ID (includes customer data).
     */
    public VehicleResponse getVehicleById(Long id) {
        log.debug("Fetching vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findByIdWithCustomer(id).orElseThrow(() -> new VehicleNotFoundException(id));

        return vehicleMapper.toResponse(vehicle);
    }

    /**
     * Get all vehicles (includes customer data).
     */
    public List<VehicleResponse> getAllVehicles() {
        log.debug("Fetching all vehicles");

        List<Vehicle> vehicles = vehicleRepository.findAllWithCustomers();
        return vehicleMapper.toResponseList(vehicles);
    }

    /**
     * Get vehicles with pagination (includes customer data).
     */
    public Page<VehicleResponse> getVehiclesWithPagination(Pageable pageable) {
        log.debug("Fetching vehicles with pagination: {}", pageable);

        Page<Vehicle> vehiclePage = vehicleRepository.findAllWithCustomers(pageable);
        return vehiclePage.map(vehicleMapper::toResponse);
    }

    /**
     * Update vehicle.
     */
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        log.debug("Updating vehicle with ID: {}", id);

        Vehicle existingVehicle = vehicleRepository.findByIdWithCustomer(id).orElseThrow(() -> new VehicleNotFoundException(id));

        // Check if VIN is changing and new VIN already exists
        if (!existingVehicle.getVin().equals(request.vin()) && vehicleRepository.existsByVin(request.vin())) {
            throw new BadRequestException("Vehicle with VIN " + request.vin() + " already exists");
        }

        // Check if customer is changing and new customer exists
        if (!existingVehicle.getCustomer().getId().equals(request.customerId())) {
            // Validate new customer exists (lightweight check)
            if (!customerRepository.existsById(request.customerId())) {
                throw new CustomerNotFoundException(request.customerId());
            }

            // Create customer reference
            Customer newCustomerRef = new Customer();
            newCustomerRef.setId(request.customerId());
            existingVehicle.setCustomer(newCustomerRef);
        }

        // Update vehicle fields
        vehicleMapper.updateEntity(existingVehicle, request);

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);

        log.info("Updated vehicle with ID: {} and VIN: {}", updatedVehicle.getId(), updatedVehicle.getVin());
        return vehicleMapper.toResponse(updatedVehicle);
    }

    /**
     * Search vehicles using filters with pagination (includes customer data).
     * Uses JPA Specifications for type-safe filtering and @EntityGraph to prevent N+1 queries.
     */
    public Page<VehicleResponse> searchVehicles(VehicleFilter filter, Pageable pageable) {
        log.debug("Searching vehicles with filter: {}, pagination: {}", filter, pageable);

        Specification<Vehicle> spec = VehicleSpecs.getVehiclesByFilters(filter);
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);
        return vehiclePage.map(vehicleMapper::toResponse);
    }

    /**
     * Delete vehicle.
     */
    @Transactional
    public void deleteVehicle(Long id) {
        log.debug("Deleting vehicle with ID: {}", id);

        int deletedCount = vehicleRepository.deleteByVehicleId(id);

        if (deletedCount == 0) {
            throw new VehicleNotFoundException(id);
        }

        log.info("Deleted vehicle with ID: {}", id);
    }
}