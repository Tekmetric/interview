package com.interview.business.controllers.recipes.payloads;

import com.interview.business.domain.Recipe;

public record RecipeResponse(
        UserInfo userInfo,
        String id,
        String image,
        String title,
        String description,
        Recipe.MealType mealType,
        Integer duration,
        Double ratingAverage,
        Long ratingCount,
        Long createdAt,
        Long updatedAt
) {

    public record UserInfo(
            String id,
            String name,
            String avatar
    ) {
    }

    public static RecipeResponse from(Recipe recipe) {
        return new RecipeResponse(
                new RecipeResponse.UserInfo(
                        recipe.user.id,
                        recipe.user.name,
                        recipe.user.avatar
                ),
                recipe.id,
                recipe.image,
                recipe.title,
                recipe.description,
                recipe.mealType,
                recipe.duration,
                recipe.ratingAverage,
                recipe.ratingCount,
                recipe.createdAt.getTime(),
                recipe.updatedAt != null ? recipe.updatedAt.getTime() : null
        );
    }
}
