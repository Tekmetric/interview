package com.interview.repository;

import com.interview.model.Animal;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides JPA Specifications for filtering Animal entities.
 * This class uses the Specification pattern from Spring Data JPA to create type-safe,
 * reusable database queries. As an alternative, we could use QueryDSL, but I've chosen
 * to stick with the Spring Data JPA Specification pattern for this exercise because it
 * requires less dependencies.
 */
public final class AnimalSpecifications {
    
    private AnimalSpecifications() {
        throw new AssertionError("No instances of AnimalSpecifications should be created - utility class");
    }

    /**
     * Creates a Specification for filtering Animals based on various criteria.
     * The method combines multiple filters using AND logic, but each filter is optional.
     * If no filters are provided (all parameters are null), returns null which means no filtering.
     *
     * @param name        Optional animal name to search for (case-insensitive, partial match)
     * @param startDate   Optional minimum date of birth (inclusive)
     * @param endDate     Optional maximum date of birth (inclusive)
     * @param employeeId  Optional ID of the responsible employee
     * @return a Specification that combines all non-null filters with AND logic, or null if no filters provided
     *
     * Example usages:
     * <pre>
     * // Find all animals with "max" in their name
     * repository.findAll(withFilters("max", null, null, null));
     *
     * // Find all animals born between dates and assigned to employee
     * repository.findAll(withFilters(null, startDate, endDate, employeeId));
     * </pre>
     */
    public static Specification<Animal> withFilters(final String name, final LocalDate startDate, 
            final LocalDate endDate, final Long employeeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    "%" + name.toLowerCase() + "%"
                ));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("dateOfBirth"), 
                    startDate
                ));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("dateOfBirth"), 
                    endDate
                ));
            }

            if (employeeId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("responsibleEmployee").get("id"), 
                    employeeId
                ));
            }

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}