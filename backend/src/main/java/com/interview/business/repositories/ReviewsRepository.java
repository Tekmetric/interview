package com.interview.business.repositories;

import com.interview.business.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Review, String>, JpaSpecificationExecutor<Review> {

    void deleteReviewsByRecipeId(String id);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.recipeId = :recipeId")
    Double findAverageRatingOf(String recipeId);

    Long countByRecipeId(String recipeId);
}
