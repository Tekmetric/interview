package com.interview.business.controllers.recipes;

import com.interview.business.controllers.recipes.payloads.RecipeResponse;
import com.interview.business.services.auth.annotation.AuthUser;
import com.interview.business.services.auth.annotation.Authenticated;
import com.interview.business.services.auth.dto.AuthUserToken;
import com.interview.business.services.recipes.RecipesService;
import com.interview.business.services.recipes.dto.*;
import com.interview.core.api.payloads.DataResponse;
import com.interview.core.api.payloads.PagedResponse;
import com.interview.core.exception.ApiException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class RecipesController {

    private final RecipesService recipesService;

    public RecipesController(RecipesService recipesService) {
        this.recipesService = recipesService;
    }

    @GetMapping("/recipes")
    public PagedResponse<RecipeResponse> getRecipes(
            @Valid RecipesFilter filters,
            @Valid RecipesSorting sorting,
            @Valid RecipesPaging paging
    ) {
        var recipes = recipesService.findRecipesBy(filters, sorting, paging);

        return new PagedResponse<>(
                recipes.get().map(RecipeResponse::from).toList(),
                paging.page(),
                paging.size(),
                recipes.getTotalElements(),
                recipes.getTotalPages()
        );
    }

    @GetMapping("/recipes/{id}")
    public DataResponse<RecipeResponse> getRecipe(@PathVariable String id) {
        var recipe = recipesService.findRecipeBy(id).orElseThrow(() -> ApiException.notFound("Recipe", id));

        return new DataResponse<>(RecipeResponse.from(recipe));
    }

    @Authenticated
    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<String> createRecipe(
            @AuthUser AuthUserToken token,
            @Valid @RequestBody RecipeCreateRequest payload
    ) {
        var recipe = recipesService.createRecipe(token.getId(), payload);

        return new DataResponse<>(recipe.id);
    }

    @Authenticated
    @PutMapping("{id}")
    public DataResponse<RecipeResponse> updateRecipe(
            @AuthUser AuthUserToken token,
            @PathVariable String id,
            @Valid @RequestBody RecipeUpdateRequest payload
    ) {
        var recipe = recipesService.findRecipeBy(id).orElseThrow(() -> ApiException.notFound("Recipe", id));

        if (!recipe.id.equals(token.getId())) {
            throw ApiException.forbidden("Recipe Update");
        }

        var updated = recipesService.updateRecipe(recipe, payload);

        return new DataResponse<>(RecipeResponse.from(updated));
    }

    @Authenticated
    @DeleteMapping("/recipes/{id}")
    public void deleteRecipe(
            @AuthUser AuthUserToken token,
            @PathVariable String id
    ) {
        var recipe = recipesService.findRecipeBy(id).orElseThrow(() -> ApiException.notFound("Recipe", id));

        if (!recipe.id.equals(token.getId())) {
            throw ApiException.forbidden("Recipe Delete");
        }

        recipesService.deleteRecipe(recipe);
    }
}
