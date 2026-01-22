package com.interview.resource;

import com.interview.dto.VehicleDTO;
import com.interview.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VehicleResource {

    private final VehicleService vehicleService;

    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) throws URISyntaxException {
        if (vehicleDTO.getId() != null) {
            throw new IllegalArgumentException("A new vehicle cannot already have an ID");
        }
        VehicleDTO result = vehicleService.create(vehicleDTO);
        return ResponseEntity.created(new URI("/api/vehicles/" + result.getId())).body(result);
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        vehicleDTO.setId(id);
        VehicleDTO result = vehicleService.update(vehicleDTO);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> partialUpdateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        vehicleDTO.setId(id);
        VehicleDTO result = vehicleService.partialUpdate(vehicleDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<VehicleDTO> list = vehicleService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable Long id) {
        VehicleDTO vehicleDTO = vehicleService.findOne(id);
        return ResponseEntity.ok(vehicleDTO);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
