package com.interview.specification;

import com.interview.dto.filter.VehicleFilter;
import com.interview.entity.Customer;
import com.interview.entity.Vehicle;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for Vehicle entity filtering.
 *
 * <p>Provides type-safe, composable filtering logic for Vehicle queries.
 * All specifications are combined using AND operations and work efficiently
 * with pagination and sorting.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VehicleSpecs {

    /**
     * Filter vehicles by customer ID.
     */
    public static Specification<Vehicle> hasCustomerId(Long customerId) {
        return (root, query, cb) -> {
            if (customerId == null) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(root.get("customer").get("id"), customerId);
        };
    }

    /**
     * Filter vehicles by VIN (exact match).
     */
    public static Specification<Vehicle> hasVin(String vin) {
        return (root, query, cb) -> {
            if (vin == null || vin.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(cb.upper(root.get("vin")), vin.trim().toUpperCase());
        };
    }

    /**
     * Filter vehicles by make (case-insensitive, partial match).
     */
    public static Specification<Vehicle> hasMake(String make) {
        return (root, query, cb) -> {
            if (make == null || make.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.like(cb.upper(root.get("make")), "%" + make.trim().toUpperCase() + "%");
        };
    }

    /**
     * Filter vehicles by model (case-insensitive, partial match).
     */
    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.like(cb.upper(root.get("model")), "%" + model.trim().toUpperCase() + "%");
        };
    }

    /**
     * Filter vehicles by year range.
     */
    public static Specification<Vehicle> hasYearBetween(Integer minYear, Integer maxYear) {
        return (root, query, cb) -> {
            if (minYear == null && maxYear == null) {
                return cb.isTrue(cb.literal(true));
            }

            if (minYear != null && maxYear != null) {
                return cb.between(root.get("year"), minYear, maxYear);
            } else if (minYear != null) {
                return cb.greaterThanOrEqualTo(root.get("year"), minYear);
            } else {
                return cb.lessThanOrEqualTo(root.get("year"), maxYear);
            }
        };
    }

    /**
     * Filter vehicles by customer email (useful for search).
     */
    public static Specification<Vehicle> hasCustomerEmail(String customerEmail) {
        return (root, query, cb) -> {
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }

            Join<Vehicle, Customer> customerJoin = root.join("customer");
            return cb.like(cb.upper(customerJoin.get("email")),
                "%" + customerEmail.trim().toUpperCase() + "%");
        };
    }

    /**
     * Filter vehicles by customer name (firstName or lastName).
     */
    public static Specification<Vehicle> hasCustomerName(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }

            Join<Vehicle, Customer> customerJoin = root.join("customer");
            String searchTerm = "%" + customerName.trim().toUpperCase() + "%";

            return cb.or(
                cb.like(cb.upper(customerJoin.get("firstName")), searchTerm),
                cb.like(cb.upper(customerJoin.get("lastName")), searchTerm)
            );
        };
    }

    /**
     * Combine multiple filters for comprehensive vehicle search using a filter object.
     *
     * @param filter VehicleFilter containing all search criteria
     * @return Combined specification
     */
    public static Specification<Vehicle> getVehiclesByFilters(VehicleFilter filter) {
        return Specification.allOf(
            hasCustomerId(filter.customerId()),
            hasVin(filter.vin()),
            hasMake(filter.make()),
            hasModel(filter.model()),
            hasYearBetween(filter.minYear(), filter.maxYear()),
            hasCustomerEmail(filter.customerEmail()),
            hasCustomerName(filter.customerName())
        );
    }
}