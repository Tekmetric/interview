package com.interview.repository.specification;

import com.interview.dto.VehicleFilterRequest;
import com.interview.model.Vehicle;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VehicleSpecification {

    public static Specification<Vehicle> withFilters(VehicleFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("owner", JoinType.LEFT);
            }

            if (filter.getBrand() != null && !filter.getBrand().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("brand")), 
                    "%" + filter.getBrand().toLowerCase() + "%"));
            }

            if (filter.getModel() != null && !filter.getModel().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("model")), 
                    "%" + filter.getModel().toLowerCase() + "%"));
            }

            if (filter.getRegistrationYear() != null) {
                predicates.add(cb.equal(root.get("registrationYear"), filter.getRegistrationYear()));
            }

            if (filter.getRegistrationYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("registrationYear"), 
                    filter.getRegistrationYearFrom()));
            }

            if (filter.getRegistrationYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("registrationYear"), 
                    filter.getRegistrationYearTo()));
            }

            if (filter.getLicensePlate() != null && !filter.getLicensePlate().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("licensePlate")), 
                    "%" + filter.getLicensePlate().toLowerCase() + "%"));
            }

            if (filter.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), filter.getOwnerId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
