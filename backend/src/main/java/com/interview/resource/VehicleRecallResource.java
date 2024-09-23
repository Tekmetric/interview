package com.interview.resource;

import com.interview.exception.ValidationException;
import com.interview.model.VehicleRecall;
import com.interview.service.VehicleRecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recalls")
public class VehicleRecallResource {
    private final VehicleRecallService vehicleRecallService;

    @Autowired
    public VehicleRecallResource(VehicleRecallService vehicleRecallService) {
        this.vehicleRecallService = vehicleRecallService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleRecall>> getAllRecalls() {
        return ResponseEntity.ok(vehicleRecallService.getAllRecalls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleRecall> getRecallById(@PathVariable Long id) {
        return vehicleRecallService.getRecallById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createRecall(@RequestBody VehicleRecall vehicleRecall) {
        try {
            VehicleRecall createdRecall = vehicleRecallService.createRecall(vehicleRecall);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRecall);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecall(@PathVariable Long id, @RequestBody VehicleRecall vehicleRecall) {
        try{
            Optional<VehicleRecall> updatedVehicleRecall = vehicleRecallService.updateRecall(id, vehicleRecall);
            return updatedVehicleRecall.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecall(@PathVariable Long id) {
        return vehicleRecallService.deleteRecall(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
