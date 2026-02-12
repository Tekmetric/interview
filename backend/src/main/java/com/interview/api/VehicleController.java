package com.interview.api;

import com.interview.api.dto.VehicleRequest;
import com.interview.api.dto.VehicleResponse;
import com.interview.application.vehicle.CreateVehicle;
import com.interview.application.vehicle.DeleteVehicle;
import com.interview.application.vehicle.GetVehicle;
import com.interview.application.vehicle.ListVehicles;
import com.interview.application.vehicle.UpdateVehicle;
import com.interview.domain.Vehicle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicles")
@Transactional
public class VehicleController {

    private final CreateVehicle createVehicle;
    private final GetVehicle getVehicle;
    private final UpdateVehicle updateVehicle;
    private final ListVehicles listVehicles;
    private final DeleteVehicle deleteVehicle;

    public VehicleController(CreateVehicle createVehicle, GetVehicle getVehicle,
                             UpdateVehicle updateVehicle, ListVehicles listVehicles,
                             DeleteVehicle deleteVehicle) {
        this.createVehicle = createVehicle;
        this.getVehicle = getVehicle;
        this.updateVehicle = updateVehicle;
        this.listVehicles = listVehicles;
        this.deleteVehicle = deleteVehicle;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = createVehicle.execute(
                request.getPlateNumber(),
                request.getModel(),
                request.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(vehicle));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        Vehicle vehicle = getVehicle.execute(id);
        return ResponseEntity.ok(toResponse(vehicle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = updateVehicle.execute(
                id,
                request.getPlateNumber(),
                request.getModel(),
                request.getCustomerId()
        );
        return ResponseEntity.ok(toResponse(vehicle));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<VehicleResponse>> list() {
        List<VehicleResponse> list = listVehicles.execute().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteVehicle.execute(id);
        return ResponseEntity.noContent().build();
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getModel(),
                vehicle.getCustomerId()
        );
    }
}
