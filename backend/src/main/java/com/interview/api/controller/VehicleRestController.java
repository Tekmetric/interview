package com.interview.api.controller;

import com.interview.api.mapper.VehicleApiMapper;
import com.interview.api.request.VehicleRequest;
import com.interview.api.request.VehicleSearchRequest;
import com.interview.api.response.VehicleResponse;
import com.interview.domain.Vehicle;
import com.interview.service.VehicleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final VehicleApiMapper vehicleApiMapper;

    public VehicleRestController(final VehicleService vehicleService, VehicleApiMapper vehicleApiMapper) {
        this.vehicleService = vehicleService;
        this.vehicleApiMapper = vehicleApiMapper;
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> getAllVehicles(
            VehicleSearchRequest searchRequest, @PageableDefault(size = 20) Pageable pageable) {
        final Page<VehicleResponse> vehicles =
                vehicleService.findAll(searchRequest.customerId(), pageable).map(vehicleApiMapper::toResponse);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleApiMapper.toResponse(vehicleService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        final Vehicle vehicle = vehicleService.create(vehicleApiMapper.toDomain(request));
        final VehicleResponse response = vehicleApiMapper.toResponse(vehicle);
        return ResponseEntity.created(URI.create("/vehicles/" + response.id())).body(response);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable UUID id, @Valid @RequestBody VehicleRequest request) {
        final Vehicle vehicle = vehicleService.update(id, vehicleApiMapper.toDomain(id, request));
        return ResponseEntity.ok(vehicleApiMapper.toResponse(vehicle));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
