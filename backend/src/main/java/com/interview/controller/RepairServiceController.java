package com.interview.controller;

import com.interview.dto.RepairServiceDTO;
import com.interview.dto.ApiResponseDTO;
import com.interview.service.RepairServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/repair-services")
@RequiredArgsConstructor
public class RepairServiceController {

    private final RepairServiceService repairServiceService;

    @PreAuthorize("hasRole('read') || hasRole('write')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> getRepairServiceById(@PathVariable Long id) {
        var repairService = repairServiceService.getRepairServiceById(id);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(repairService)
                .message("Repair service retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('read') || hasRole('write')")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<RepairServiceDTO>>> getAllRepairServices(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        var sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        var pageable = PageRequest.of(pageNumber, pageSize, sort);
        var repairServicePage = repairServiceService.getAllRepairServices(pageable);

        var response = ApiResponseDTO.<Page<RepairServiceDTO>>builder()
                .success(true)
                .data(repairServicePage)
                .message("Repair services retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('write')")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> createRepairService(@Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        var createdService = repairServiceService.createRepairService(repairServiceDTO);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(createdService)
                .message("Repair service created successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('write')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> updateRepairService(
            @PathVariable Long id,
            @Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        var updatedService = repairServiceService.updateRepairService(id, repairServiceDTO);

        var response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(updatedService)
                .message("Repair service updated successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRepairService(@PathVariable Long id) {
        repairServiceService.deleteRepairService(id);
        
        var response = ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Repair service deleted successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }
}
