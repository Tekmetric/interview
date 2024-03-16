package com.interview.mock;

import com.interview.business.domain.AppUser;
import com.interview.business.domain.Recipe;
import com.interview.business.domain.Review;
import com.interview.business.services.reviews.ReviewsService;
import com.interview.business.services.reviews.dto.ReviewCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class ReviewMocksService {
    private final String Review_Message = """
            I made no changes. Everyone has different levels of heat at medium and I had to use medium high. The other variable is weight of chicken and my six pieces were fairly thick. So keep your eye on things and be patient it’s well worth it.
            """;

    private final ReviewsService reviewsService;

    public ReviewMocksService(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    public List<Review> generateReviews(List<AppUser> users, List<Recipe> recipes, int timesPerUserPerRecipe) {
        return users.stream().flatMap(user -> recipes.stream().flatMap(recipe ->
                IntStream.range(0, timesPerUserPerRecipe).mapToObj((i) ->
                        reviewsService.createReview(user.id, recipe.id, new ReviewCreateRequest(
                                new Random().nextInt(5) + 1,
                                Review_Message
                        ))
                )
        )).toList();
    }
}
