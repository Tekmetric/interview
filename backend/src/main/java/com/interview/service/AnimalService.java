package com.interview.service;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.VetDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Service interface for managing animals.
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
 *   <li>Individual animals are cached by ID in the 'animals' cache</li>
 *   <li>Filtered search results are cached in 'animals-filtered' with composite keys</li>
 *   <li>Animal-vet relationships are cached in 'animal-vets' by animal ID and page</li>
 * </ul>
 *
 * <h2>Note on Cache Management</h2>
 * <p>
 * The current implementation uses a simplified caching approach that favors consistency
 * over granularity. A more fine-grained approach would require:
 * </p>
 * <ol>
 *   <li>Custom cache key generator to track which filtered results contain each animal</li>
 *   <li>Custom cache implementation to track entity relationships</li>
 *   <li>Additional metadata storage to maintain these relationships</li>
 * </ol>
 *
 * <p>
 * This could be achieved by:
 * </p>
 * <ul>
 *   <li>Storing metadata about which filter combinations matched each entity</li>
 *   <li>Using composite key structures for relationships</li>
 *   <li>Implementing custom cache key generators</li>
 * </ul>
 *
 * <p>
 * However, given the current requirements and data volume, the simpler approach of broader
 * cache eviction provides a good balance between consistency and complexity.
 * </p>
 */
public interface AnimalService {
    /**
     * Creates a new animal.
     *
     * @param animalDTO the DTO containing the animal's information
     * @return the created animal as a DTO
     */
    @CacheEvict(value = "animals-filtered", allEntries = true)
    AnimalDTO create(CreateAnimalDTO animalDTO);
    
    /**
     * Finds an animal by its ID.
     *
     * @param id the ID of the animal to find
     * @return the found animal as a DTO
     * @throws javax.persistence.EntityNotFoundException if the animal is not found
     */
    @Cacheable(value = "animals", key = "#id")
    AnimalDTO findById(Long id);
    
    /**
     * Updates an existing animal.
     *
     * @param id the ID of the animal to update
     * @param animalDTO the DTO containing the updated information
     * @return the updated animal as a DTO
     * @throws javax.persistence.EntityNotFoundException if the animal is not found
     */
    @Caching(
        put = { @CachePut(value = "animals", key = "#id") },
        evict = {
            @CacheEvict(value = "animals-filtered", allEntries = true),
            @CacheEvict(value = "animal-vets", allEntries = true),
            @CacheEvict(value = "vets-page", allEntries = true),
            @CacheEvict(value = "vets", allEntries = true),
            @CacheEvict(value = "employees-page", allEntries = true),
            @CacheEvict(value = "employees", allEntries = true)
        }
    )
    AnimalDTO update(Long id, AnimalDTO animalDTO);
    
    /**
     * Deletes an animal by its ID.
     *
     * @param id the ID of the animal to delete
     * @throws javax.persistence.EntityNotFoundException if the animal is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "animals", key = "#id"),
        @CacheEvict(value = "animals-filtered", allEntries = true),
        @CacheEvict(value = "animal-vets", allEntries = true),
        @CacheEvict(value = "vets", allEntries = true),
        @CacheEvict(value = "vets-page", allEntries = true)
    })
    void delete(Long id);
    
    /**
     * Finds all vets associated with a specific animal.
     *
     * @param animalId the ID of the animal
     * @param pageable the pagination information
     * @return a page of vets associated with the animal
     * @throws javax.persistence.EntityNotFoundException if the animal is not found
     */
    @Cacheable(value = "animal-vets", key = "#animalId + '_' + #pageable.pageNumber")
    Page<VetDTO> findVetsByAnimalId(Long animalId, Pageable pageable);
    
    /**
     * Finds animals based on various filters.
     *
     * @param name the name to filter by (optional)
     * @param startDate the start date to filter by (optional)
     * @param endDate the end date to filter by (optional)
     * @param employeeId the employee ID to filter by (optional)
     * @param pageable the pagination information
     * @return a page of animals matching the filters
     * @throws javax.persistence.EntityNotFoundException if the specified employee is not found
     */
    @Cacheable(value = "animals-filtered", 
               key = "#name + '_' + #startDate + '_' + #endDate + '_' + #employeeId + '_' + " +
                     "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort",
               unless = "#result.totalElements == 0")
    Page<AnimalDTO> findByFilters(String name, LocalDate startDate, LocalDate endDate, Long employeeId, Pageable pageable);
}