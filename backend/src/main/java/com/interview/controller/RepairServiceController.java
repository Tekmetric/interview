package com.interview.controller;

import com.interview.dto.RepairServiceDTO;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.ValidationException;
import com.interview.service.RepairServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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
    public ResponseEntity<RepairServiceDTO> createRepairService(@Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        RepairServiceDTO createdService = repairServiceService.createRepairService(repairServiceDTO);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

    /**
     * Get a repair service by its ID.
     *
     * @param id the ID of the repair service to retrieve
     * @return the repair service
     */
    @GetMapping("/{id}")
    public ResponseEntity<RepairServiceDTO> getRepairServiceById(@PathVariable Long id) {
        try {
            RepairServiceDTO repairService = repairServiceService.getRepairServiceById(id);
            return ResponseEntity.ok(repairService);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all repair services with pagination.
     *
     * @param pageable pagination information (page, size, sort)
     * @return the page of repair services
     */
    @GetMapping
    public ResponseEntity<Page<RepairServiceDTO>> getAllRepairServices(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<RepairServiceDTO> repairServicePage = repairServiceService.getAllRepairServices(pageable);
        return ResponseEntity.ok(repairServicePage);
    }

    /**
     * Update an existing repair service.
     *
     * @param id the ID of the repair service to update
     * @param repairServiceDTO the updated repair service data
     * @return the updated repair service
     */
    @PutMapping("/{id}")
    public ResponseEntity<RepairServiceDTO> updateRepairService(
            @PathVariable Long id,
            @Valid @RequestBody RepairServiceDTO repairServiceDTO) {
        try {
            RepairServiceDTO updatedService = repairServiceService.updateRepairService(id, repairServiceDTO);
            return ResponseEntity.ok(updatedService);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a repair service by its ID.
     *
     * @param id the ID of the repair service to delete
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairService(@PathVariable Long id) {
        try {
            repairServiceService.deleteRepairService(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Handle validation exceptions and return appropriate error responses.
     *
     * @param ex the validation exception
     * @return a map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
