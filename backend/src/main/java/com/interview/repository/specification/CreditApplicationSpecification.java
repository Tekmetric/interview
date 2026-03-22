package com.interview.repository.specification;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.enums.ApplicationStatus;

@NoArgsConstructor
public final class CreditApplicationSpecification {

    public static Specification<CreditApplication> hasStatus(final ApplicationStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}
