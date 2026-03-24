package com.interview.repository;

import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.Vehicle;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class VehicleSpecification {
    public static Specification<Vehicle> fromCriteria(VehicleSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.unrestricted();
        }

        Specification<Vehicle> specification = Specification.unrestricted();
        specification = append(specification, equalsIfPresent("vin", criteria.getVin()));
        specification = append(specification, equalsIfPresent("licensePlate", criteria.getLicensePlate()));
        specification = append(specification, equalsIfPresent("make", criteria.getMake()));
        specification = append(specification, equalsIfPresent("model", criteria.getModel()));
        specification = append(specification, equalsIfPresent("modelYear", criteria.getYear()));

        return specification;
    }

    public static Specification<Vehicle> ownedBy(Long ownerId) {
        if (ownerId == null) {
            return Specification.unrestricted();
        }

        return (root, query, builder) -> builder.equal(root.get("owner").get("id"), ownerId);
    }

    private static Specification<Vehicle> equalsIfPresent(String fieldName, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String stringValue && !StringUtils.hasText(stringValue)) {
            return null;
        }

        return (root, query, builder) -> builder.equal(root.get(fieldName), value);
    }

    private static Specification<Vehicle> append(Specification<Vehicle> left, Specification<Vehicle> right) {
        if (left == null) {
            return right;
        }

        return right == null ? left : left.and(right);
    }
}
