package com.interview.service;

import com.interview.exception.VehicleNotFoundException;
import com.interview.model.api.CreateVehicleRequest;
import com.interview.model.api.UpdateVehicleRequest;
import com.interview.model.api.Vehicle;
import com.interview.model.entity.VehicleEntity;
import com.interview.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides services to create, retrieve, update, and delete vehicle records.
 */
@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Create a new vehicle
     *
     * @param request the create vehicle request
     * @return the id of the created vehicle
     */
    public String createVehicle(CreateVehicleRequest request) {
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setMake(request.getMake());
        vehicleEntity.setModel(request.getModel());
        vehicleEntity.setYear(request.getYear());
        vehicleEntity.setVin(request.getVin());
        return vehicleRepository.save(vehicleEntity).getId();
    }

    /**
     * Retrieve all vehicles
     *
     * @return list of vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::transformEntityToVehicle)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific vehicle
     *
     * @param id the id of the vehicle to retrieve
     * @return the vehicle
     * @throws VehicleNotFoundException if the vehicle does not exist
     */
    public Vehicle getVehicle(String id) {
        return vehicleRepository.findById(id)
                .map(this::transformEntityToVehicle)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
    }

    /**
     * Delete a vehicle.
     *
     * @param id the id of the vehicle to delete
     * @throws VehicleNotFoundException if the vehicle does not exist
     */
    public void deleteVehicle(String id) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        vehicleRepository.delete(vehicleEntity);
    }

    /**
     * Update the information for a vehicle. Only the vehicle fields that are non-null
     * in the request will be updated.
     *
     * @param request the updated vehicle information
     * @param id      the id of the vehicle to update
     * @return the updated vehicle
     * @throws VehicleNotFoundException if the vehicle does not exist
     */
    public Vehicle updateVehicle(UpdateVehicleRequest request,
                                 String id) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        if (request.getYear() != null) {
            vehicleEntity.setYear(request.getYear());
        }
        if (request.getMake() != null) {
            vehicleEntity.setMake(request.getMake());
        }
        if (request.getModel() != null) {
            vehicleEntity.setModel(request.getModel());
        }
        if (request.getVin() != null) {
            vehicleEntity.setVin(request.getVin());
        }
        vehicleRepository.save(vehicleEntity);
        return transformEntityToVehicle(vehicleEntity);
    }

    private Vehicle transformEntityToVehicle(VehicleEntity vehicleEntity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleEntity.getId());
        vehicle.setMake(vehicleEntity.getMake());
        vehicle.setModel(vehicleEntity.getModel());
        vehicle.setYear(vehicleEntity.getYear());
        vehicle.setVin(vehicleEntity.getVin());
        return vehicle;
    }

}
