package com.interview.resource;

import com.interview.model.dto.VehicleDTO;
import com.interview.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle")
public class VehicleResource {

    private final VehicleService vehicleService;

    @GetMapping("/{vin}")
    public ResponseEntity<VehicleDTO> getByVin(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.getVehicleByVin(vin));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<VehicleDTO> addVehicleToCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody VehicleDTO vehicleDto) {
        vehicleService.addVehicleToCustomer(customerId, vehicleDto);
        return new ResponseEntity<>(
                vehicleService.addVehicleToCustomer(customerId, vehicleDto),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{vin}/customer/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVehicleFromCustomer(
            @PathVariable Long customerId,
            @PathVariable String vin) {
        vehicleService.removeVehicleFromCustomer(customerId, vin);
    }
}