package com.interview.controller;


import com.interview.dto.ServiceAppointmentDTO;
import com.interview.service.ServiceAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/serviceappointments")
public class ServiceAppointmentController {

    @Autowired
    private ServiceAppointmentService serviceAppointmentService;

    @PostMapping
    public ServiceAppointmentDTO createServiceAppointment(@Valid @RequestBody ServiceAppointmentDTO serviceAppointmentDTO) {
        return serviceAppointmentService.createServiceAppointment(serviceAppointmentDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceAppointmentDTO> getServiceAppointmentById(@PathVariable Long id) {
        return serviceAppointmentService.getServiceAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ServiceAppointmentDTO> getAllServiceAppointments() {
        return serviceAppointmentService.getAllServiceAppointments();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceAppointmentDTO> updateServiceAppointment(@PathVariable Long id, @Valid @RequestBody ServiceAppointmentDTO serviceAppointmentDetails) {
        ServiceAppointmentDTO updatedServiceAppointment = serviceAppointmentService.updateServiceAppointment(id, serviceAppointmentDetails);
        return ResponseEntity.ok(updatedServiceAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceAppointment(@PathVariable Long id) {
        serviceAppointmentService.deleteServiceAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
