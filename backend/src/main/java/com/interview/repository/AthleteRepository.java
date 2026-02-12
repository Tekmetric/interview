package com.interview.repository;

import com.interview.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for {@link Athlete} entities with support for specifications, pagination, and sorting.
 */
public interface AthleteRepository extends JpaRepository<Athlete, Long>, JpaSpecificationExecutor<Athlete> {

    /**
     * Checks if an athlete exists with the given first name, last name, and birth timestamp.
     *
     * @param firstName the athlete's first name
     * @param lastName the athlete's last name
     * @param birthTimestamp the athlete's birth timestamp
     * @return true if athlete exists, false otherwise
     */
    boolean existsByFirstNameAndLastNameAndBirthTimestamp(String firstName, String lastName, Long birthTimestamp);
}
