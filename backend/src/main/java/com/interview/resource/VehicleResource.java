package com.interview.resource;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleResource {

    private final VehicleService vehicleService;

    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse created = vehicleService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> list(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {

        return ResponseEntity.ok(vehicleService.findAll(make, year, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> get(@PathVariable UUID id) {

        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request) {

        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        vehicleService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}