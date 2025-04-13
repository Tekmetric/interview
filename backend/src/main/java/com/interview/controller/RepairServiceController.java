package com.interview.controller;

import com.interview.dto.RepairServiceDTO;
import com.interview.dto.ApiResponseDTO;
import com.interview.service.RepairServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST controller for managing repair service operations.
 */
@RestController
@RequestMapping("/api/repair-services")
@RequiredArgsConstructor
public class RepairServiceController {

    private final RepairServiceService repairServiceService;

    /**
     * Create a new repair service.
     *
     * @param repairServiceDTO the repair service to create
     * @return the created repair service
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> createRepairService(@Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        RepairServiceDTO createdService = repairServiceService.createRepairService(repairServiceDTO);
        
        ApiResponseDTO<RepairServiceDTO> response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(createdService)
                .message("Repair service created successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a repair service by its ID.
     *
     * @param id the ID of the repair service to retrieve
     * @return the repair service
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> getRepairServiceById(@PathVariable Long id) {
        RepairServiceDTO repairService = repairServiceService.getRepairServiceById(id);
        
        ApiResponseDTO<RepairServiceDTO> response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(repairService)
                .message("Repair service retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    /**
     * Get all repair services with pagination.
     *
     * @param pageNumber the page number (zero-based)
     * @param pageSize the size of the page
     * @param sortBy the field to sort by (default: "id")
     * @param sortDirection the direction to sort ("asc" or "desc")
     * @return the page of repair services
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<RepairServiceDTO>>> getAllRepairServices(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<RepairServiceDTO> repairServicePage = repairServiceService.getAllRepairServices(pageable);
        
        ApiResponseDTO<Page<RepairServiceDTO>> response = ApiResponseDTO.<Page<RepairServiceDTO>>builder()
                .success(true)
                .data(repairServicePage)
                .message("Repair services retrieved successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing repair service.
     *
     * @param id the ID of the repair service to update
     * @param repairServiceDTO the updated repair service data
     * @return the updated repair service
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RepairServiceDTO>> updateRepairService(
            @PathVariable Long id,
            @Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        RepairServiceDTO updatedService = repairServiceService.updateRepairService(id, repairServiceDTO);
        
        ApiResponseDTO<RepairServiceDTO> response = ApiResponseDTO.<RepairServiceDTO>builder()
                .success(true)
                .data(updatedService)
                .message("Repair service updated successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a repair service by its ID.
     *
     * @param id the ID of the repair service to delete
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRepairService(@PathVariable Long id) {
        repairServiceService.deleteRepairService(id);
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Repair service deleted successfully")
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(response);
    }
    

}
