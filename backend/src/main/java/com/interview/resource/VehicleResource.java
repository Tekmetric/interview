package com.interview.resource;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleResource {

    private final VehicleService vehicleService;

    public VehicleResource(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public Page<VehicleResponse> search(Pageable pageable) {
        return vehicleService.search(pageable);
    }

    @GetMapping("/{id}")
    public VehicleResponse get(@PathVariable UUID id) {
        return vehicleService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse add(@RequestBody VehicleRequest vehicleRequest) {
        return vehicleService.add(vehicleRequest);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable UUID id,
            @RequestBody VehicleRequest vehicleRequest) {
        return vehicleService.update(id, vehicleRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        vehicleService.delete(id);
    }
}
