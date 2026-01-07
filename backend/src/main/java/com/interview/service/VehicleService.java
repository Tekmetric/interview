package com.interview.service;

import com.interview.dto.VehicleDTO;
import com.interview.entity.CustomerEntity;
import com.interview.entity.VehicleEntity;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<VehicleDTO> getAllVehicles(String vin, Pageable pageable) {
        log.info("Fetching vehicles with pagination - page: {}, size: {}, vin: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), vin);
        
        if (vin != null && !vin.isBlank()) {
            return vehicleRepository.findByVinContainingIgnoreCase(vin, pageable)
                    .map(VehicleMapper::toDTO);
        }
        
        return vehicleRepository.findAll(pageable)
                .map(VehicleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long id) {
        log.info("Fetching vehicle with id: {}", id);
        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        return VehicleMapper.toDTO(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByCustomerId(Long customerId) {
        log.info("Fetching vehicles for customer id: {}", customerId);
        return vehicleRepository.findByCustomerId(customerId).stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        log.info("Creating new vehicle with VIN: {}", vehicleDTO.getVin());
        
        if (vehicleRepository.existsByVin(vehicleDTO.getVin())) {
            throw new DuplicateResourceException("Vehicle", "VIN", vehicleDTO.getVin());
        }
        
        CustomerEntity customer = customerRepository.findById(vehicleDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", vehicleDTO.getCustomerId()));
        
        VehicleEntity vehicle = VehicleMapper.toEntity(vehicleDTO);
        vehicle.setCustomer(customer);
        
        VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
        
        log.info("Vehicle created successfully with id: {}", savedVehicle.getId());
        return VehicleMapper.toDTO(savedVehicle);
    }

    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        log.info("Updating vehicle with id: {}", id);
        
        VehicleEntity existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        
        if (!existingVehicle.getVin().equals(vehicleDTO.getVin()) 
                && vehicleRepository.existsByVin(vehicleDTO.getVin())) {
            throw new DuplicateResourceException("Vehicle", "VIN", vehicleDTO.getVin());
        }
        
        if (!existingVehicle.getCustomer().getId().equals(vehicleDTO.getCustomerId())) {
            CustomerEntity newCustomer = customerRepository.findById(vehicleDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", vehicleDTO.getCustomerId()));
            existingVehicle.setCustomer(newCustomer);
        }
        
        VehicleMapper.updateEntityFromDTO(vehicleDTO, existingVehicle);
        VehicleEntity updatedVehicle = vehicleRepository.save(existingVehicle);
        
        log.info("Vehicle updated successfully with id: {}", id);
        return VehicleMapper.toDTO(updatedVehicle);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        log.info("Deleting vehicle with id: {}", id);
        
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", id);
        }
        
        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted successfully with id: {}", id);
    }
}
