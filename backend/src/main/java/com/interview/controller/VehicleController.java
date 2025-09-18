package com.interview.controller;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.service.VehicleService;
import com.interview.validation.VehicleValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public PagedModel<VehicleResponse> search(Pageable pageable) {
        Page<VehicleResponse> page = vehicleService.search(pageable);
        return new PagedModel<>(page);
    }

    @GetMapping("/{id}")
    public VehicleResponse get(@PathVariable UUID id) {
        return vehicleService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse add(@RequestBody VehicleRequest vehicleRequest) {
        VehicleValidator.validate(vehicleRequest);
        return vehicleService.add(vehicleRequest);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable UUID id,
            @RequestBody VehicleRequest vehicleRequest) {
        VehicleValidator.validate(vehicleRequest);
        return vehicleService.update(id, vehicleRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        vehicleService.delete(id);
    }
}
