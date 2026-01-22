package com.interview.resource;

import com.interview.dto.ServiceJobDTO;
import com.interview.service.ServiceJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceJobResource {

    private final ServiceJobService serviceJobService;

    public ServiceJobResource(ServiceJobService serviceJobService) {
        this.serviceJobService = serviceJobService;
    }

    @PostMapping("/service-jobs")
    public ResponseEntity<ServiceJobDTO> createServiceJob(@RequestBody ServiceJobDTO serviceJobDTO) throws URISyntaxException {
        if (serviceJobDTO.getId() != null) {
            throw new IllegalArgumentException("A new service job cannot already have an ID");
        }
        ServiceJobDTO result = serviceJobService.save(serviceJobDTO);
        return ResponseEntity.created(new URI("/api/service-jobs/" + result.getId())).body(result);
    }

    @PutMapping("/service-jobs/{id}")
    public ResponseEntity<ServiceJobDTO> updateServiceJob(@PathVariable Long id, @RequestBody ServiceJobDTO serviceJobDTO) {
        serviceJobDTO.setId(id);
        ServiceJobDTO result = serviceJobService.save(serviceJobDTO);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/service-jobs/{id}")
    public ResponseEntity<ServiceJobDTO> partialUpdateServiceJob(@PathVariable Long id, @RequestBody ServiceJobDTO serviceJobDTO) {
        serviceJobDTO.setId(id);
        ServiceJobDTO result = serviceJobService.partialUpdate(serviceJobDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/service-jobs")
    public ResponseEntity<List<ServiceJobDTO>> getAllServiceJobs() {
        List<ServiceJobDTO> list = serviceJobService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/service-jobs/{id}")
    public ResponseEntity<ServiceJobDTO> getServiceJob(@PathVariable Long id) {
        ServiceJobDTO serviceJobDTO = serviceJobService.findOne(id);
        return ResponseEntity.ok(serviceJobDTO);
    }

    @DeleteMapping("/service-jobs/{id}")
    public ResponseEntity<Void> deleteServiceJob(@PathVariable Long id) {
        serviceJobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
