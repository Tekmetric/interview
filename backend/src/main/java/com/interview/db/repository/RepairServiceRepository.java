package com.interview.db.repository;

import com.interview.db.entity.RepairService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

/**
 * Repository interface for RepairService entity providing CRUD operations.
 */
@Repository
public interface RepairServiceRepository extends CrudRepository<RepairService, Long>, PagingAndSortingRepository<RepairService, Long> {
    
    /**
     * Find a repair service by its ID.
     * 
     * @param id the ID of the repair service
     * @return an Optional containing the repair service if found, or empty if not found
     */
    @Override
    Optional<RepairService> findById(Long id);
    
    /**
     * Find all repair services with pagination.
     * 
     * @param pageable pagination information
     * @return a page of repair services
     */
    @Override
    Page<RepairService> findAll(Pageable pageable);
    
    /**
     * Save a repair service.
     * 
     * @param repairService the repair service to save
     * @return the saved repair service
     */
    @Override
    <S extends RepairService> S save(S repairService);
    
    /**
     * Delete a repair service by its ID.
     * 
     * @param id the ID of the repair service to delete
     */
    @Override
    void deleteById(Long id);
}
