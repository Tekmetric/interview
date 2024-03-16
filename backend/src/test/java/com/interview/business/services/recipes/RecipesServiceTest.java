package com.interview.business.services.recipes;

import com.interview.business.domain.AppUser;
import com.interview.business.domain.Recipe;
import com.interview.business.repositories.ReviewsRepository;
import com.interview.business.services.recipes.dto.RecipeCreateRequest;
import com.interview.business.services.recipes.dto.RecipeUpdateRequest;
import com.interview.business.services.reviews.ReviewsService;
import com.interview.business.services.reviews.dto.ReviewCreateRequest;
import com.interview.business.services.users.UsersService;
import com.interview.business.services.users.dto.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipesServiceTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RecipesService recipesService;

    @Autowired
    private ReviewsService reviewsService;

    @Autowired
    private ReviewsRepository reviewsRepository;

    private AppUser signUp() {
        var request = new SignUpRequest("Kaan", "kaan@email.com", "password", "AVATAR");

        return usersService.signUp(request);
    }

    @Test
    public void creatingRecipeSetsRatingAs0() {
        var user = signUp();

        var request = new RecipeCreateRequest("Title", "Description", "Image", 12, Recipe.MealType.BREAD);

        var recipe = recipesService.createRecipe(user.id, request);

        assertThat(recipe.ratingAverage).isEqualTo(0);
        assertThat(recipe.ratingCount).isEqualTo(0);
    }

    @Test
    public void updatingRecipeOnlyUpdatesGivenFields() {
        var user = signUp();

        var request = new RecipeCreateRequest("Title", "Description", "Image", 12, Recipe.MealType.BREAD);

        var recipe = recipesService.createRecipe(user.id, request);

        var updated = recipesService.updateRecipe(recipe, RecipeUpdateRequest.builder().mealType(Recipe.MealType.BREAKFAST).build());

        assertThat(updated.title).isEqualTo(request.title());
        assertThat(updated.description).isEqualTo(request.description());
        assertThat(updated.mealType).isNotEqualTo(request.mealType());
    }

    @Test
    public void deletingRecipeDeletesAllReviews() {
        var user = signUp();

        var request = new RecipeCreateRequest("Title", "Description", "Image", 12, Recipe.MealType.BREAD);

        var reviewsBefore = reviewsRepository.findAll();
        assertThat(reviewsBefore).hasSize(0);

        var recipe1 = recipesService.createRecipe(user.id, request);
        var recipe2 = recipesService.createRecipe(user.id, request);

        reviewsService.createReview(user.id, recipe1.id, new ReviewCreateRequest(1, "M"));
        reviewsService.createReview(user.id, recipe2.id, new ReviewCreateRequest(1, "M"));

        var reviewsAfter = reviewsRepository.findAll();
        assertThat(reviewsAfter).hasSize(2);

        recipesService.deleteRecipe(recipe1);

        var reviewsEnd = reviewsRepository.findAll();
        assertThat(reviewsEnd).hasSize(1);
    }

    @Test
    public void updateRatingsCalculatesAndSetsCorrectRatings() {
        var user = signUp();

        var request = new RecipeCreateRequest("Title", "Description", "Image", 12, Recipe.MealType.BREAD);

        var reviewsBefore = reviewsRepository.findAll();
        assertThat(reviewsBefore).hasSize(0);

        var recipe1 = recipesService.createRecipe(user.id, request);
        assertThat(recipe1.ratingAverage).isEqualTo(0);
        assertThat(recipe1.ratingCount).isEqualTo(0);

        reviewsService.createReview(user.id, recipe1.id, new ReviewCreateRequest(1, "M"));
        reviewsService.createReview(user.id, recipe1.id, new ReviewCreateRequest(5, "M"));

        recipesService.updateRatings(recipe1.id);

        var updated = recipesService.findRecipeBy(recipe1.id).orElse(null);

        assertThat(updated).isNotNull();
        assertThat(updated.ratingAverage).isEqualTo(3);
        assertThat(updated.ratingCount).isEqualTo(2);
    }

}