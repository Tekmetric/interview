package com.interview.service;

import com.interview.dto.VehicleDTO;
import com.interview.mapper.VehicleMapper;
import com.interview.model.Vehicle;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    public VehicleService(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Create a vehicle.
     *
     * @param vehicleDTO the entity to save.
     * @return the persisted entity.
     */
    public VehicleDTO create(VehicleDTO vehicleDTO) {
        Vehicle vehicle = VehicleMapper.toEntity(vehicleDTO);
        if (vehicleDTO.getCustomerId() != null) {
            com.interview.model.Customer customer = customerRepository.findById(vehicleDTO.getCustomerId())
                .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found with id: " + vehicleDTO.getCustomerId()));
            vehicle.setCustomer(customer);
        }
        vehicle = vehicleRepository.save(vehicle);
        return VehicleMapper.toDto(vehicle);
    }

    /**
     * Update a vehicle.
     *
     * @param vehicleDTO the entity to update.
     * @return the persisted entity.
     */
    public VehicleDTO update(VehicleDTO vehicleDTO) {
        return vehicleRepository.findById(vehicleDTO.getId())
            .map(existingVehicle -> {
                existingVehicle.setVin(vehicleDTO.getVin());
                existingVehicle.setMake(vehicleDTO.getMake());
                existingVehicle.setModel(vehicleDTO.getModel());
                existingVehicle.setModelYear(vehicleDTO.getModelYear());
                if (vehicleDTO.getCustomerId() != null) {
                    com.interview.model.Customer customer = customerRepository.findById(vehicleDTO.getCustomerId())
                        .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found with id: " + vehicleDTO.getCustomerId()));
                    existingVehicle.setCustomer(customer);
                }
                return vehicleRepository.save(existingVehicle);
            })
            .map(VehicleMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found!"));
    }

    /**
     * Partially updates a vehicle.
     *
     * @param vehicleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public VehicleDTO partialUpdate(VehicleDTO vehicleDTO) {
        return vehicleRepository.findById(vehicleDTO.getId())
            .map(existingVehicle -> {
                if (vehicleDTO.getVin() != null) {
                    existingVehicle.setVin(vehicleDTO.getVin());
                }
                if (vehicleDTO.getMake() != null) {
                    existingVehicle.setMake(vehicleDTO.getMake());
                }
                if (vehicleDTO.getModel() != null) {
                    existingVehicle.setModel(vehicleDTO.getModel());
                }
                if (vehicleDTO.getModelYear() != null) {
                    existingVehicle.setModelYear(vehicleDTO.getModelYear());
                }
                if (vehicleDTO.getCustomerId() != null) {
                    com.interview.model.Customer customer = customerRepository.findById(vehicleDTO.getCustomerId())
                        .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Customer not found with id: " + vehicleDTO.getCustomerId()));
                    existingVehicle.setCustomer(customer);
                }
                return vehicleRepository.save(existingVehicle);
            })
            .map(VehicleMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found!"));
    }


    /**
     * Get all the vehicles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
            .map(VehicleMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Get one vehicle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public VehicleDTO findOne(Long id) {
        return vehicleRepository.findById(id)
            .map(VehicleMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found!"));
    }

    /**
     * Delete the vehicle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }
}
