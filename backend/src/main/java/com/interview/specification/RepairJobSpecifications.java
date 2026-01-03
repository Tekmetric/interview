package com.interview.specification;

import com.interview.model.RepairJob;
import org.springframework.data.jpa.domain.Specification;

public class RepairJobSpecifications {

    public static Specification<RepairJob> userIdEquals(String userId) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("userId")), userId.toLowerCase());
    }

    public static Specification<RepairJob> statusEquals(com.interview.model.RepairStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<RepairJob> licensePlate(String make) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("licensePlate")), make.toLowerCase());
    }
}

