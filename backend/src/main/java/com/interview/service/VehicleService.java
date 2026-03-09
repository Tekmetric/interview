package com.interview.service;

import com.interview.domain.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.repository.entity.VehicleEntity;
import com.interview.repository.mapper.VehicleEntityMapper;
import com.interview.service.exception.VehicleNotFound;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {
    final VehicleEntityMapper vehicleEntityMapper;
    final VehicleRepository vehicleRepository;

    public VehicleService(VehicleEntityMapper vehicleEntityMapper, VehicleRepository vehicleRepository) {
        this.vehicleEntityMapper = vehicleEntityMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAll(UUID customerId, Pageable pageable) {
        // would use jpa spec if params grew beyond a couple different options
        final Page<VehicleEntity> page = customerId != null
                ? vehicleRepository.findAllByCustomerId(customerId, pageable)
                : vehicleRepository.findAll(pageable);
        return page.map(vehicleEntityMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(UUID id) {
        final VehicleEntity entity = vehicleRepository.findById(id).orElseThrow(() -> new VehicleNotFound(id));
        return vehicleEntityMapper.toDomain(entity);
    }

    @Transactional
    public Vehicle create(Vehicle vehicle) {
        final VehicleEntity vehicleEntity = vehicleEntityMapper.toEntity(vehicle);
        final VehicleEntity saved = vehicleRepository.save(vehicleEntity);
        return vehicleEntityMapper.toDomain(saved);
    }

    @Transactional
    public Vehicle update(UUID id, Vehicle vehicle) {
        final VehicleEntity entity = vehicleRepository.findById(id).orElseThrow(() -> new VehicleNotFound(id));
        vehicleEntityMapper.updateEntity(vehicle, entity);
        final VehicleEntity saved = vehicleRepository.save(entity);
        return vehicleEntityMapper.toDomain(saved);
    }

    @Transactional
    public void delete(UUID id) {
        final VehicleEntity entity = vehicleRepository.findById(id).orElseThrow(() -> new VehicleNotFound(id));
        vehicleRepository.delete(entity);
    }
}
