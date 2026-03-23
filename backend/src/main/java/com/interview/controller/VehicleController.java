package com.interview.controller;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.dto.VehicleSearchCriteria;
import com.interview.mapper.VehicleMapper;
import com.interview.entity.Vehicle;
import com.interview.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private static final int MAX_PAGE_SIZE = 50;

    private final VehicleService vehicleService;

    @GetMapping
    @ResponseBody
    public Page<VehicleResponse> getVehicles(@Valid @ModelAttribute VehicleSearchCriteria criteria, Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not exceed " + MAX_PAGE_SIZE);
        }

        return vehicleService.findAll(criteria, pageable)
                .map(VehicleMapper::toResponse);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public VehicleResponse getVehicle(@PathVariable Long id) {
        return VehicleMapper.toResponse(vehicleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse createVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.create(vehicle));
    }

    @PutMapping("/{id}")
    public VehicleResponse updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleService.update(id, vehicle));
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}
