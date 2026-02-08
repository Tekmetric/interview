package com.interview.resource;

import com.interview.dto.ServiceJobDTO;
import com.interview.dto.VehicleDTO;
import com.interview.service.ServiceJobService;
import com.interview.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class VehicleResource {

    private final VehicleService vehicleService;
    private final ServiceJobService serviceJobService;

    public VehicleResource(VehicleService vehicleService, ServiceJobService serviceJobService) {
        this.vehicleService = vehicleService;
        this.serviceJobService = serviceJobService;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) throws URISyntaxException {
        if (vehicleDTO.getId() != null) {
            throw new IllegalArgumentException("A new vehicle cannot already have an ID");
        }
        VehicleDTO result = vehicleService.create(vehicleDTO);
        return ResponseEntity.created(new URI("/api/vehicles/" + result.getId())).body(result);
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDTO vehicleDTO) {
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
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(Pageable pageable) {
        Page<VehicleDTO> page = vehicleService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable Long id) {
        VehicleDTO vehicleDTO = vehicleService.findOne(id);
        return ResponseEntity.ok(vehicleDTO);
    }

    @GetMapping("/vehicles/{id}/service-jobs")
    public ResponseEntity<Page<ServiceJobDTO>> getServiceJobsForVehicle(@PathVariable Long id, Pageable pageable) {
        Page<ServiceJobDTO> page = serviceJobService.findByVehicleId(id, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
