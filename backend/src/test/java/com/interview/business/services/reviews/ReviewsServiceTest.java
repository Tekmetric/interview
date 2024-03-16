package com.interview.business.services.reviews;

import com.interview.business.domain.AppUser;
import com.interview.business.domain.Recipe;
import com.interview.business.repositories.ReviewsRepository;
import com.interview.business.services.recipes.RecipesService;
import com.interview.business.services.recipes.dto.RecipeCreateRequest;
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
class ReviewsServiceTest {

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
    public void creatingAndDeletingReviewCallsToUpdateRatings() {
        var user = signUp();

        var request = new RecipeCreateRequest("Title", "Description", "Image", 12, Recipe.MealType.BREAD);

        var reviewsBefore = reviewsRepository.findAll();
        assertThat(reviewsBefore).hasSize(0);

        var recipe1 = recipesService.createRecipe(user.id, request);
        assertThat(recipe1.ratingAverage).isEqualTo(0);
        assertThat(recipe1.ratingCount).isEqualTo(0);

        var review = reviewsService.createReview(user.id, recipe1.id, new ReviewCreateRequest(1, "M"));
        reviewsService.createReview(user.id, recipe1.id, new ReviewCreateRequest(5, "M"));

        recipesService.updateRatings(recipe1.id);

        var afterCreate = recipesService.findRecipeBy(recipe1.id).orElse(null);

        assertThat(afterCreate).isNotNull();
        assertThat(afterCreate.ratingAverage).isEqualTo(3);
        assertThat(afterCreate.ratingCount).isEqualTo(2);

        reviewsService.deleteReview(review);

        var afterDelete = recipesService.findRecipeBy(recipe1.id).orElse(null);

        assertThat(afterDelete).isNotNull();
        assertThat(afterDelete.ratingAverage).isEqualTo(5);
        assertThat(afterDelete.ratingCount).isEqualTo(1);
    }

}