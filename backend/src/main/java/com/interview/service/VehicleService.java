package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.model.dto.VehicleDTO;
import com.interview.model.entity.Customer;
import com.interview.model.entity.Vehicle;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleDTO getVehicleByVin(String vin) {
        return vehicleRepository.findByVin(vin)
                .map(vehicleMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with vin: " + vin));
    }

    public VehicleDTO addVehicleToCustomer(Long customerId, VehicleDTO vehicleDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        customer.addVehicle(vehicle);
        customerRepository.save(customer);
        return vehicleMapper.toDTO(vehicle);
    }

    public void removeVehicleFromCustomer(Long customerId, String vin) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with vin: " + vin));

        customer.removeVehicle(vehicle);
        customerRepository.save(customer);
    }
}