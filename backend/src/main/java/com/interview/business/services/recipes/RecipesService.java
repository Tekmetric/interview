package com.interview.business.services.recipes;

import com.interview.business.domain.Recipe;
import com.interview.business.repositories.RecipesRepository;
import com.interview.business.repositories.ReviewsRepository;
import com.interview.business.services.recipes.dto.*;
import com.interview.core.exception.ApiException;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RecipesService {

    private final RecipesRepository repository;
    private final ReviewsRepository reviewsRepository;

    public RecipesService(RecipesRepository repository, ReviewsRepository reviewsRepository) {
        this.repository = repository;
        this.reviewsRepository = reviewsRepository;
    }

    public boolean notExistsBy(@Nonnull String id) {
        return !repository.existsById(id);
    }

    public Optional<Recipe> findRecipeBy(@Nonnull String id) {
        return repository.findById(id);
    }

    public Page<Recipe> findRecipesBy(@Nonnull RecipesFilter filter, @Nonnull RecipesSorting sorting, @Nonnull RecipesPaging paging) {
        return repository.findAll(filter.toSpec(), PageRequest.of(paging.page(), paging.size(), sorting.toSort()));
    }

    @Transactional
    public Recipe createRecipe(@Nonnull String userId, @Nonnull RecipeCreateRequest dto) {
        var recipe = Recipe.builder()
                .userId(userId)
                .mealType(dto.mealType())
                .image(dto.image())
                .title(dto.title())
                .description(dto.description())
                .duration(dto.duration())
                .ratingAverage(0.0)
                .ratingCount(0L)
                .createdAt(new Date())
                .build();

        return repository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(@NotNull Recipe recipe, @Nonnull RecipeUpdateRequest dto) {
        recipe.title = dto.title() != null ? dto.title() : recipe.title;
        recipe.description = dto.description() != null ? dto.description() : recipe.description;
        recipe.duration = dto.duration() != null ? dto.duration() : recipe.duration;
        recipe.mealType = dto.mealType() != null ? dto.mealType() : recipe.mealType;
        recipe.updatedAt = new Date();

        return repository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(@NotNull Recipe recipe) {
        reviewsRepository.deleteReviewsByRecipeId(recipe.id);
        repository.delete(recipe);
    }

    @Transactional
    public void updateRatings(String recipeId) {
        var recipe = repository.findById(recipeId).orElseThrow(() -> ApiException.notFound("Recipe", recipeId));

        var average = reviewsRepository.findAverageRatingOf(recipeId);
        var total = reviewsRepository.countByRecipeId(recipeId);

        recipe.ratingAverage = average;
        recipe.ratingCount = total;

        repository.save(recipe);
    }
}
