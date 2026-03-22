package com.interview.controller;

import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/{id}")
    @ResponseBody
    public Vehicle getVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);

        if (vehicle.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return vehicle.get();
    }
}
