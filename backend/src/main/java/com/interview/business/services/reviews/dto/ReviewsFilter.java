package com.interview.business.services.reviews.dto;

import com.interview.business.domain.Review;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public record ReviewsFilter(
        @Nullable
        String keyword,

        @Nullable
        @Range(min = 1, max = 5)
        Integer rating
) {

    public Specification<Review> toSpec() {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (keyword != null) {
                var expression = "%" + this.keyword.toLowerCase() + "%";

                var messagePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(Review.Fields.message)), expression);

                predicates.add(criteriaBuilder.or(messagePredicate));
            }

            if (rating != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Review.Fields.rating), this.rating));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
