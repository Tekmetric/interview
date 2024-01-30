package com.interview.resource;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.resource.dto.VehicleCreationRequest;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleUpdateRequest;
import com.interview.service.VehicleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles")
public class VehicleResource {

    private final VehicleService service;

    @GetMapping
    public ResponseEntity<Page<VehicleDto>> getVehicles(
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "createdAt") String sortField,
        @RequestParam(defaultValue = "ASC") String sortOrder
    ) {
        Direction direction = Direction.DESC.name().equals(sortOrder) ? Direction.DESC : Direction.ASC;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, direction, sortField);
        Page<VehicleDto> vehicles = service.getVehicles(pageRequest);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicle(
        @PathVariable Long id
    ) {
        VehicleDto vehicle = service.getVehicle(id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(
        @Valid @RequestBody VehicleCreationRequest request
    ) {
        VehicleDto vehicle = service.createVehicle(request);
        return ResponseEntity.created(getLocation(vehicle)).body(vehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(
        @PathVariable Long id,
        @Valid @RequestBody VehicleUpdateRequest request
    ) {
        VehicleDto vehicle = service.updateVehicle(id, request);
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteVehicle(
        @PathVariable Long id
    ) {
        Long vehicle = service.deleteVehicle(id);
        return ResponseEntity.ok(vehicle);
    }

    private URI getLocation(VehicleDto vehicle) {
        return ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(vehicle.getId())
            .toUri();
    }
}
