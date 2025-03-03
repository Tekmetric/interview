package com.interview.service;

import com.interview.dto.CreateVetDTO;
import com.interview.dto.VetDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing veterinarians.
 *
 * <h2>Implementation Note</h2>
 * <p>
 * While Spring generally recommends placing caching annotations on implementation classes,
 * they are intentionally placed on this interface because:
 * </p>
 * <ul>
 *   <li>The caching strategy is considered part of the service contract</li>
 *   <li>There is a single implementation of this interface</li>
 *   <li>This approach provides better visibility of the caching behavior</li>
 * </ul>
 *
 * <h2>Caching Strategy</h2>
 * <ul>
 *   <li>Individual vets are cached by ID in the 'vets' cache</li>
 *   <li>Paginated results are cached in 'vets-page' by page number</li>
 *   <li>Animal-vet relationships are cached in 'animal-vets' by animal ID and page</li>
 * </ul>
 *
 * <h2>Note on Cache Management</h2>
 * <p>
 * The current implementation uses a simplified caching approach that favors consistency
 * over granularity. A more fine-grained approach would require:
 * </p>
 * <ol>
 *   <li>Custom cache key generator to track which page contains each vet</li>
 *   <li>Custom cache implementation to track entity relationships</li>
 *   <li>Additional metadata storage to maintain these relationships</li>
 * </ol>
 *
 * <p>
 * This could be achieved by:
 * </p>
 * <ul>
 *   <li>Storing metadata about page membership for each entity</li>
 *   <li>Using composite key structures for relationships</li>
 *   <li>Implementing custom cache key generators</li>
 * </ul>
 *
 * <p>
 * However, given the current requirements and data volume, the simpler approach of broader
 * cache eviction provides a good balance between consistency and complexity.
 * </p>
 */
public interface VetService {
    /**
     * Creates a new veterinarian.
     *
     * @param vetDTO the DTO containing the vet's information
     * @return the created vet as a DTO
     */
    @Caching(evict = {
        @CacheEvict(value = "vets-page", allEntries = true),
        @CacheEvict(value = "animal-vets", allEntries = true)
    })
    VetDTO create(CreateVetDTO vetDTO);
    
    /**
     * Finds a veterinarian by their ID.
     *
     * @param id the ID of the vet to find
     * @return the found vet as a DTO
     * @throws javax.persistence.EntityNotFoundException if the vet is not found
     */
    @Cacheable(value = "vets", key = "#id")
    VetDTO findById(Long id);
    
    /**
     * Retrieves all veterinarians with pagination support.
     *
     * @param pageable the pagination information
     * @return a page of vets
     */
    @Cacheable(value = "vets-page", 
               key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    Page<VetDTO> findAll(Pageable pageable);
    
    /**
     * Updates an existing veterinarian.
     *
     * @param id the ID of the vet to update
     * @param vetDTO the DTO containing the updated information
     * @return the updated vet as a DTO
     * @throws javax.persistence.EntityNotFoundException if the vet is not found
     */
    @Caching(
        put = { @CachePut(value = "vets", key = "#id") },
        evict = {
            @CacheEvict(value = "vets-page", allEntries = true),
            @CacheEvict(value = "animal-vets", allEntries = true)
        }
    )
    VetDTO update(Long id, VetDTO vetDTO);
    
    /**
     * Deletes a veterinarian by their ID.
     *
     * @param id the ID of the vet to delete
     * @throws javax.persistence.EntityNotFoundException if the vet is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "vets", key = "#id"),
        @CacheEvict(value = "vets-page", allEntries = true),
        @CacheEvict(value = "animal-vets", allEntries = true),
        @CacheEvict(value = "animals", allEntries = true),
        @CacheEvict(value = "animals-filtered", allEntries = true)
    })
    void delete(Long id);
}