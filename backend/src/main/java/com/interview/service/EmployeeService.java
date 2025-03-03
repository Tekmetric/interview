package com.interview.service;

import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.EmployeeDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing employees.
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
 *   <li>Individual employees are cached by ID in the 'employees' cache</li>
 *   <li>Paginated results are cached in 'employees-page' by page number</li>
 *   <li>Changes to employees may affect 'animals-filtered' cache due to employee-animal relationships</li>
 * </ul>
 *
 * <h2>Note on Cache Management</h2>
 * <p>
 * The current implementation uses a simplified caching approach that favors consistency
 * over granularity. A more fine-grained approach would require:
 * </p>
 * <ol>
 *   <li>Custom cache key generator to track which page contains each employee</li>
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
public interface EmployeeService {
    /**
     * Creates a new employee.
     *
     * @param employeeDTO the DTO containing the employee's information
     * @return the created employee as a DTO
     */
    @CacheEvict(value = "employees-page", allEntries = true)
    EmployeeDTO create(CreateEmployeeDTO employeeDTO);
    
    /**
     * Finds an employee by their ID.
     *
     * @param id the ID of the employee to find
     * @return the found employee as a DTO
     * @throws javax.persistence.EntityNotFoundException if the employee is not found
     */
    @Cacheable(value = "employees", key = "#id")
    EmployeeDTO findById(Long id);
    
    /**
     * Retrieves all employees with pagination support.
     *
     * @param pageable the pagination information
     * @return a page of employees
     */
    @Cacheable(value = "employees-page", 
               key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    Page<EmployeeDTO> findAll(Pageable pageable);
    
    /**
     * Updates an existing employee.
     *
     * @param id the ID of the employee to update
     * @param employeeDTO the DTO containing the updated information
     * @return the updated employee as a DTO
     * @throws javax.persistence.EntityNotFoundException if the employee is not found
     */
    @Caching(
        put = { @CachePut(value = "employees", key = "#id") },
        evict = {
            @CacheEvict(value = "employees-page", allEntries = true),
            @CacheEvict(value = "animals-filtered", allEntries = true)
        }
    )
    EmployeeDTO update(Long id, EmployeeDTO employeeDTO);
    
    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     * @throws javax.persistence.EntityNotFoundException if the employee is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "employees", key = "#id"),
        @CacheEvict(value = "employees-page", allEntries = true),
        @CacheEvict(value = "animals-filtered", allEntries = true)
    })
    void delete(Long id);
}