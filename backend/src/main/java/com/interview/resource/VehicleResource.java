package com.interview.resource;

import com.interview.model.api.*;
import com.interview.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Provides endpoints to create, retrieve, update, and delete vehicle records.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleResource {

    @Autowired
    public VehicleService vehicleService;

    /**
     * Create a vehicle.
     *
     * @param request the create vehicle request
     * @return created vehicle location and id
     */
    @PostMapping
    public ResponseEntity<CreateVehicleResponse> createVehicle(@RequestBody @Valid CreateVehicleRequest request) {
        String id = vehicleService.createVehicle(request);
        CreateVehicleResponse response = new CreateVehicleResponse();
        response.setId(id);
        response.setLocation("/api/vehicles/" + id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve all vehicles.
     *
     * @return list of vehicles
     */
    @GetMapping
    public ResponseEntity<GetAllVehiclesResponse> getAllVehicles() {
        GetAllVehiclesResponse response = new GetAllVehiclesResponse();
        response.setVehicles(vehicleService.getAllVehicles());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a specific vehicle.
     *
     * @param id the id of the vehicle to retrieve
     * @return the vehicle
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(vehicleService.getVehicle(id));
    }

    /**
     * Delete a vehicle.
     *
     * @param id the id of the vehicle to delete
     * @return empty response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable(name = "id") String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update a vehicle.
     *
     * @param request the vehicle information to update
     * @param id      the id of the vehicle to update
     * @return the updated vehicle
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@RequestBody @Valid UpdateVehicleRequest request,
                                                 @PathVariable(name = "id") String id) {
        return ResponseEntity.ok(
                vehicleService.updateVehicle(request, id)
        );
    }

}