package com.interview.business.services.recipes.dto;

import com.interview.business.domain.Recipe;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public record RecipesFilter(
        @Nullable String userId,
        @Nullable String keyword,
        @Nullable Recipe.MealType mealType
) {
    public Specification<Recipe> toSpec() {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get(Recipe.Fields.userId), this.userId));
            }

            if (keyword != null) {
                var expression = "%" + this.keyword.toLowerCase() + "%";

                var titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(Recipe.Fields.title)), expression);
                var descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(Recipe.Fields.description)), expression);

                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }

            if (mealType != null) {
                predicates.add(criteriaBuilder.equal(root.get(Recipe.Fields.mealType), this.mealType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
