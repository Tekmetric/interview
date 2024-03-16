package com.interview.business.services.recipes.dto;

import com.interview.business.domain.Recipe.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record RecipeCreateRequest(
        @NotNull
        @Size(min = 5, max = 64)
        String title,

        @NotNull
        @Size(min = 20, max = 20000)
        String description,

        @NotNull
        String image,

        @NotNull
        @Range(min = 1)
        Integer duration,

        @NotNull
        MealType mealType
) {

}
