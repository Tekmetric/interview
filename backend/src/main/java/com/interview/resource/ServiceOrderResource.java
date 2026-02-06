package com.interview.resource;

import com.interview.model.dto.ServiceOrderDTO;
import com.interview.model.enums.ServiceOrderStatus;
import com.interview.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
public class ServiceOrderResource {

    private final ServiceOrderService serviceOrderService;

    @GetMapping("/vehicle/{vin}")
    public ResponseEntity<Page<ServiceOrderDTO>> getOrdersByVehicle(
            @PathVariable String vin,
            Pageable pageable) {
        return ResponseEntity.ok(serviceOrderService.getServiceOrdersByVehicle(vin, pageable));
    }

    @GetMapping("/vehicle/{vin}/status/{status}")
    public ResponseEntity<Page<ServiceOrderDTO>> getOrdersByVehicleAndStatus(
            @PathVariable String vin,
            @PathVariable ServiceOrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(serviceOrderService.getServiceOrdersByVehicleAndStatus(vin, status, pageable));
    }

    @PostMapping("/vehicle/{vin}")
    public ResponseEntity<ServiceOrderDTO> addOrder(
            @PathVariable String vin,
            @Valid @RequestBody ServiceOrderDTO dto) {
        return new ResponseEntity<>(serviceOrderService.addServiceOrder(vin, dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody ServiceOrderDTO dto) {
        return ResponseEntity.ok(serviceOrderService.updateServiceOrder(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceOrderDTO> updatePartialOrder(
            @PathVariable Long id,
            @Valid @RequestBody ServiceOrderDTO dto) {
        return ResponseEntity.ok(serviceOrderService.updateServiceOrder(id, dto));
    }

    @DeleteMapping("/{id}/vehicle/{vin}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrderFromVehicle(@PathVariable Long id, @PathVariable String vin) {
        serviceOrderService.removeServiceOrderFromVehicle(vin, id);
    }
}