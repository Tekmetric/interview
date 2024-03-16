package com.interview.business.services.recipes.dto;

import com.interview.business.domain.Recipe;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record RecipeUpdateRequest(
        @Nullable
        @Size(min = 5, max = 64)
        String title,

        @Nullable
        @Size(min = 20, max = 20000)
        String description,

        @Nullable
        @Range(min = 1)
        Integer duration,

        @Nullable
        Recipe.MealType mealType
) {

}