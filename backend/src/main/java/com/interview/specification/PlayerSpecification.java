package com.interview.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.interview.entity.Player;
import com.interview.util.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class PlayerSpecification implements Specification<Player> {

    private final SearchCriteria searchCriteria;

    public PlayerSpecification(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate
        (Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (searchCriteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                root.<String>get(searchCriteria.getKey()), searchCriteria.getValue().toString());
        } else if (searchCriteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                root.<String>get(searchCriteria.getKey()), searchCriteria.getValue().toString());
        } else if (searchCriteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(searchCriteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                    root.<String>get(searchCriteria.getKey()), "%" + searchCriteria.getValue() + "%");
            } else {
                return builder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
            }
        }
        return null;
    }
}
