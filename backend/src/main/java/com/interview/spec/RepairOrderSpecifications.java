package com.interview.spec;

import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class RepairOrderSpecifications {
    private RepairOrderSpecifications() {}

    public static Specification<RepairOrder> hasStatus(RepairOrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
    public static Specification<RepairOrder> vinContains(String vinPart) {
        return (root, query, cb) -> (vinPart == null || vinPart.isBlank()) ? null : cb.like(cb.lower(root.get("vin")), "%" + vinPart.toLowerCase() + "%");
    }
    public static Specification<RepairOrder> makeEquals(String make) {
        return (root, query, cb) -> (make == null || make.isBlank()) ? null : cb.equal(cb.lower(root.get("vehicleMake")), make.toLowerCase());
    }
    public static Specification<RepairOrder> modelEquals(String model) {
        return (root, query, cb) -> (model == null || model.isBlank()) ? null : cb.equal(cb.lower(root.get("vehicleModel")), model.toLowerCase());
    }
    public static Specification<RepairOrder> createdBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }
}
