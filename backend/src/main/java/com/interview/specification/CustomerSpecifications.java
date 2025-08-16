package com.interview.specification;

import com.interview.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecifications {

    public static Specification<Customer> fuzzySearchLastName(final String lastname) {
        return (root, cq, builder) -> {
            if (lastname == null || lastname.trim().isEmpty()) {
                return builder.conjunction(); // always true, returns all
            }
            final String likePattern = "%" + lastname.toLowerCase() + "%";
            return builder.like(builder.lower(root.get("lastName")), likePattern);
        };
    }
}