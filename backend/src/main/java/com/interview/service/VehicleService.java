package com.interview.service;

import com.interview.converter.VehicleConverter;
import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Page<VehicleResponse> search(Pageable pageable, Map<String, String> requestParams) {
        return VehicleConverter.toPredicate(requestParams)
                .map(predicate -> vehicleRepository.findAll(predicate, pageable))
                .orElse(vehicleRepository.findAll(pageable))
                .map(VehicleConverter::toVehicleResponse);
    }

    public VehicleResponse get(UUID id) {
        Vehicle vehicle = vehicleRepository.findByIdOrThrow(id);
        return VehicleConverter.toVehicleResponse(vehicle);
    }

    public VehicleResponse add(VehicleRequest vehicleRequest) {
        Vehicle vehicle = VehicleConverter.toVehicle(vehicleRequest);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return VehicleConverter.toVehicleResponse(savedVehicle);
    }

    public VehicleResponse update(UUID id, VehicleRequest vehicleRequest) {
        Vehicle vehicle = vehicleRepository.findByIdOrThrow(id);
        Vehicle updatedVehicle = VehicleConverter.apply(vehicle, vehicleRequest);
        Vehicle savedVehicle = vehicleRepository.save(updatedVehicle);
        return VehicleConverter.toVehicleResponse(savedVehicle);
    }

    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }
}
