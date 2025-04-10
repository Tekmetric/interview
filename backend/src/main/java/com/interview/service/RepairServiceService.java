package com.interview.service;

import com.interview.db.entity.RepairService;
import com.interview.db.repository.RepairServiceRepository;
import com.interview.dto.RepairServiceDTO;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.RepairServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service class for managing repair service operations.
 * Handles business logic and provides a layer between controllers and repositories.
 */
@Service
@RequiredArgsConstructor
public class RepairServiceService {

    private final RepairServiceRepository repairServiceRepository;
    private final RepairServiceMapper repairServiceMapper;

    /**
     * Create a new repair service.
     *
     * @param repairServiceDTO the repair service data
     * @return the created repair service DTO with generated ID
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public RepairServiceDTO createRepairService(RepairServiceDTO repairServiceDTO) {
        // Convert DTO to entity
        RepairService repairService = repairServiceMapper.toEntity(repairServiceDTO);
        
        // Save entity
        RepairService savedRepairService = repairServiceRepository.save(repairService);
        
        // Convert saved entity back to DTO and return
        return repairServiceMapper.toDto(savedRepairService);
    }

    /**
     * Get a repair service by its ID.
     *
     * @param id the repair service ID
     * @return the repair service DTO if found
     * @throws ResourceNotFoundException if the repair service is not found
     */
    public RepairServiceDTO getRepairServiceById(Long id) {
        Optional<RepairService> repairServiceOptional = repairServiceRepository.findById(id);
        
        if (repairServiceOptional.isPresent()) {
            return repairServiceMapper.toDto(repairServiceOptional.get());
        } else {
            throw ResourceNotFoundException.forId("Repair service", id);
        }
    }

    /**
     * Get all repair services with pagination.
     *
     * @param pageable pagination information
     * @return page of repair service DTOs
     */
    public Page<RepairServiceDTO> getAllRepairServices(Pageable pageable) {
        Page<RepairService> repairServicePage = repairServiceRepository.findAll(pageable);
        
        return repairServicePage.map(repairServiceMapper::toDto);
    }

    /**
     * Update an existing repair service.
     *
     * @param id the ID of the repair service to update
     * @param repairServiceDTO the updated repair service data
     * @return the updated repair service DTO
     * @throws ResourceNotFoundException if the repair service is not found
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public RepairServiceDTO updateRepairService(Long id, RepairServiceDTO repairServiceDTO) {
        // Check if the repair service exists
        if (!repairServiceRepository.existsById(id)) {
            throw ResourceNotFoundException.forId("Repair service", id);
        }
        
        // Set the ID to ensure we're updating the correct entity
        repairServiceDTO.setId(id);
        
        // Convert DTO to entity
        RepairService repairService = repairServiceMapper.toEntity(repairServiceDTO);
        
        // Save the updated entity
        RepairService updatedRepairService = repairServiceRepository.save(repairService);
        
        // Convert updated entity back to DTO and return
        return repairServiceMapper.toDto(updatedRepairService);
    }

    /**
     * Delete a repair service by its ID.
     *
     * @param id the ID of the repair service to delete
     * @throws ResourceNotFoundException if the repair service is not found
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteRepairService(Long id) {
        // Check if the repair service exists
        if (!repairServiceRepository.existsById(id)) {
            throw ResourceNotFoundException.forId("Repair service", id);
        }
        
        repairServiceRepository.deleteById(id);
    }
}
