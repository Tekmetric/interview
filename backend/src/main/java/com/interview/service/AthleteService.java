package com.interview.service;

import com.interview.model.Athlete;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Service for managing {@link Athlete} business logic with support for filtering, pagination, and sorting.
 */
public interface AthleteService {

    /**
     * Retrieves all athletes.
     *
     * @return list of all athletes
     */
    List<Athlete> findAll();

    /**
     * Retrieves athletes matching the specification with pagination.
     *
     * @param spec     filter criteria
     * @param pageable pagination and sorting parameters
     * @return page of filtered athletes
     */
    Page<Athlete> findAll(Specification<Athlete> spec, Pageable pageable);

    /**
     * Retrieves an athlete by ID.
     *
     * @param id the athlete's ID
     * @return the athlete
     * @throws com.interview.exception.AthleteNotFoundException if not found
     */
    Athlete findById(Long id);

    /**
     * Saves an athlete (creates new or updates existing).
     *
     * @param athlete the athlete to save
     * @return the saved athlete
     */
    Athlete save(Athlete athlete);

    /**
     * Deletes an athlete by ID.
     *
     * @param id the athlete's ID
     */
    void deleteById(Long id);
}
