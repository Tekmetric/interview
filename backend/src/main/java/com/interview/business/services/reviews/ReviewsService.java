package com.interview.business.services.reviews;

import com.interview.business.domain.Review;
import com.interview.business.repositories.ReviewsRepository;
import com.interview.business.services.recipes.RecipesService;
import com.interview.business.services.reviews.dto.ReviewCreateRequest;
import com.interview.business.services.reviews.dto.ReviewsFilter;
import com.interview.business.services.reviews.dto.ReviewsPaging;
import com.interview.business.services.reviews.dto.ReviewsSorting;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReviewsService {

    private final ReviewsRepository repository;
    private final RecipesService recipesService;

    public ReviewsService(ReviewsRepository repository, RecipesService recipesService) {
        this.repository = repository;
        this.recipesService = recipesService;
    }

    public Optional<Review> findReviewBy(@Nonnull String id) {
        return repository.findById(id);
    }

    public Page<Review> findReviewsBy(
            @Nonnull String recipeId,
            @Nonnull ReviewsFilter filter,
            @Nonnull ReviewsSorting sorting,
            @Nonnull ReviewsPaging paging
    ) {
        final var withIdSpec = filter.toSpec().and((r, q, c) -> c.equal(r.get(Review.Fields.recipeId), recipeId));

        return repository.findAll(withIdSpec, PageRequest.of(paging.page(), paging.size(), sorting.toSort()));
    }

    @Transactional
    public Review createReview(String userId, String recipeId, ReviewCreateRequest payload) {
        var review = Review.builder()
                .userId(userId)
                .recipeId(recipeId)
                .message(payload.message())
                .rating(payload.rating())
                .createdAt(new Date())
                .build();

        var saved = repository.save(review);


        recipesService.updateRatings(recipeId);

        return saved;
    }

    @Transactional
    public void deleteReview(Review review) {
        repository.delete(review);

        recipesService.updateRatings(review.recipeId);
    }
}
