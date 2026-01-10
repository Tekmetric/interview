package com.interview.controller;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.model.Vehicle;
import com.interview.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest req) {
        Vehicle v = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VehicleResponse(v));
    }

    @GetMapping
    public Page<VehicleResponse> getAll(Pageable pageable) {
        return service.getAll(pageable).map(VehicleResponse::new);
    }

    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable Long id) {
        return new VehicleResponse(service.getById(id));
    }

    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest req) {
        return new VehicleResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
