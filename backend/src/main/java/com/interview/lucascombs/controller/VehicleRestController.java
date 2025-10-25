package com.interview.lucascombs.controller;

import com.interview.lucascombs.mapping.VehicleResourceMapper;
import com.interview.lucascombs.resource.VehicleResource;
import com.interview.lucascombs.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleRestController {

    private final VehicleResourceMapper vehicleResourceMapper;
    private final VehicleService vehicleService;

    @Autowired
    public VehicleRestController(final VehicleResourceMapper vehicleResourceMapper,
                                 final VehicleService vehicleService) {
        this.vehicleResourceMapper = vehicleResourceMapper;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResource>> getAll(@RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(vehicleService.getAll(page, size).stream()
                .map(vehicleResourceMapper::toResource)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public VehicleResource getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(vehicleResourceMapper::toResource)
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<VehicleResource> create(final @RequestBody VehicleResource vehicleResource) {
        return ResponseEntity.ok(vehicleResourceMapper.toResource(
                vehicleService.save(vehicleResourceMapper.toEntity(vehicleResource)))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResource> update(@PathVariable Long id, final @RequestBody VehicleResource vehicleResource) {
        return vehicleService.getById(id)
                .map(storedInDb -> vehicleResourceMapper.updateEntity(vehicleResource, storedInDb))
                .map(vehicleService::save)
                .map(vehicleResourceMapper::toResource)
                .map(ResponseEntity::ok)
                .orElse(null);
    }
}
