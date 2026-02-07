package com.interview.specification;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.allOf;

public class RepairJobSpecifications {

    public static Specification<RepairJob> queryAll(String userId, RepairStatus status, String licensePlate) {
        Specification<RepairJob> repairJobSpecification = allOf();

        if (userId != null) {
            repairJobSpecification = repairJobSpecification.and(userIdEquals(userId));
        }

        if (status != null) {
            repairJobSpecification = repairJobSpecification.and(statusEquals(status));
        }

        if (licensePlate != null) {
            repairJobSpecification = repairJobSpecification.and(licensePlate(licensePlate));
        }

        return repairJobSpecification;
    }

    private static Specification<RepairJob> userIdEquals(String userId) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("userId")), userId.toLowerCase());
    }

    private static Specification<RepairJob> statusEquals(com.interview.model.RepairStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    private static Specification<RepairJob> licensePlate(String make) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("licensePlate")), make.toLowerCase());
    }
}

