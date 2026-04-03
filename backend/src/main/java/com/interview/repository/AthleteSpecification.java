package com.interview.repository;

import com.interview.model.Athlete;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable JPA Specifications for filtering {@link Athlete} entities.
 * All methods perform case-insensitive partial matching and return null for blank inputs.
 */
public class AthleteSpecification {

    /**
     * Filters athletes by nationality (case-insensitive partial match).
     *
     * @param nationality the nationality to filter by
     * @return specification for filtering by nationality, or null if blank
     */
    public static Specification<Athlete> hasNationality(String nationality) {
        return (root, query, criteriaBuilder) -> {
            if (nationality == null || nationality.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nationality")), "%" + nationality.toLowerCase() + "%");
        };
    }

    /**
     * Filters athletes by discipline (case-insensitive partial match).
     *
     * @param discipline the discipline to filter by
     * @return specification for filtering by discipline, or null if blank
     */
    public static Specification<Athlete> hasDiscipline(String discipline) {
        return (root, query, criteriaBuilder) -> {
            if (discipline == null || discipline.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("discipline")), "%" + discipline.toLowerCase() + "%");
        };
    }

    /**
     * Searches athletes by first or last name (case-insensitive partial match).
     *
     * @param search the search term to match against names
     * @return specification for searching by name, or null if blank
     */
    public static Specification<Athlete> nameContains(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern));
        };
    }
}
