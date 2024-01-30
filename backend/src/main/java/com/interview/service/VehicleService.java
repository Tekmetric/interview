package com.interview.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interview.exception.LicensePlateExistException;
import com.interview.exception.VehicleNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.resource.dto.VehicleCreationRequest;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

    public Page<VehicleDto> getVehicles(PageRequest pageRequest) {
        Page<Vehicle> vehicles = vehicleRepository.findByDeletedAtIsNull(pageRequest);
        return vehicles.map(vehicleMapper::toDto);
    }

    public VehicleDto getVehicle(Long id) {
        return vehicleRepository.findByIdAndDeletedAtIsNull(id)
            .map(vehicleMapper::toDto)
            .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public VehicleDto createVehicle(VehicleCreationRequest request) {
        vehicleRepository
            .findByLicensePlateAndDeletedAtIsNull(request.getLicensePlate())
            .ifPresent((vehicle) -> {
                throw new LicensePlateExistException(vehicle.getLicensePlate());
            });

        Vehicle detachedVehicle = vehicleMapper.toDomainEntity(request);
        return vehicleMapper.toDto(vehicleRepository.save(detachedVehicle));
    }

    public VehicleDto updateVehicle(Long id, VehicleUpdateRequest request) {
        Vehicle attachedVehicle = vehicleRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new VehicleNotFoundException(id));

        vehicleRepository
            .findByLicensePlateAndDeletedAtIsNull(request.getLicensePlate())
            .filter(vehicle -> !vehicle.getId().equals(attachedVehicle.getId()))
            .ifPresent((vehicle) -> {
                throw new LicensePlateExistException(vehicle.getLicensePlate());
            });
        
        Vehicle detachedVehicle = vehicleMapper.toDomainEntity(attachedVehicle, request);
        return vehicleMapper.toDto(vehicleRepository.save(detachedVehicle));
    }

    public Long deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new VehicleNotFoundException(id));
        vehicle.setDeletedAt(Instant.now());
        return vehicleRepository.save(vehicle).getId();
    }
}
