package com.interview.business.controllers.reviews;

import com.interview.business.controllers.reviews.payloads.ReviewResponse;
import com.interview.business.services.auth.annotation.AuthUser;
import com.interview.business.services.auth.annotation.Authenticated;
import com.interview.business.services.auth.dto.AuthUserToken;
import com.interview.business.services.recipes.RecipesService;
import com.interview.business.services.reviews.ReviewsService;
import com.interview.business.services.reviews.dto.ReviewCreateRequest;
import com.interview.business.services.reviews.dto.ReviewsFilter;
import com.interview.business.services.reviews.dto.ReviewsPaging;
import com.interview.business.services.reviews.dto.ReviewsSorting;
import com.interview.core.api.payloads.DataResponse;
import com.interview.core.api.payloads.PagedResponse;
import com.interview.core.exception.ApiException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewsController {

    private final ReviewsService reviewsService;
    private final RecipesService recipesService;

    public ReviewsController(ReviewsService reviewsService, RecipesService recipesService) {
        this.reviewsService = reviewsService;
        this.recipesService = recipesService;
    }

    @GetMapping("/recipes/{recipeId}/reviews")
    public PagedResponse<ReviewResponse> getReviews(
            @PathVariable String recipeId,
            @Valid ReviewsFilter filters,
            @Valid ReviewsSorting sorting,
            @Valid ReviewsPaging paging
    ) {
        if (recipesService.notExistsBy(recipeId)) {
            throw ApiException.notFound("Recipe", recipeId);
        }

        var reviews = reviewsService.findReviewsBy(recipeId, filters, sorting, paging);

        return new PagedResponse<>(
                reviews.get().map(ReviewResponse::from).toList(),
                paging.page(),
                paging.size(),
                reviews.getTotalElements(),
                reviews.getTotalPages()
        );
    }

    @Authenticated
    @PostMapping("/recipes/{recipeId}/reviews")
    public DataResponse<String> createReview(
            @AuthUser AuthUserToken token,
            @PathVariable String recipeId,
            @Valid @RequestBody ReviewCreateRequest payload
    ) {
        if (recipesService.notExistsBy(recipeId)) {
            throw ApiException.notFound("Recipe", recipeId);
        }

        var review = reviewsService.createReview(token.getId(), recipeId, payload);

        return new DataResponse<>(review.id);
    }

    @Authenticated
    @DeleteMapping("/recipes/{recipeId}/reviews/{id}")
    public void deleteReview(
            @AuthUser AuthUserToken token,
            @PathVariable String recipeId,
            @PathVariable String id
    ) {
        var review = reviewsService.findReviewBy(id).orElseThrow(() -> ApiException.notFound("Review", id));

        if (!review.userId.equals(token.getId())) {
            throw ApiException.forbidden("Review Delete");
        }

        reviewsService.deleteReview(review);
    }
}
