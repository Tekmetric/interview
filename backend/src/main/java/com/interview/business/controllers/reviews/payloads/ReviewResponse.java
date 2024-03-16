package com.interview.business.controllers.reviews.payloads;

import com.interview.business.domain.Review;

public record ReviewResponse(
        UserInfo userInfo,
        String id,
        String message,
        Integer rating,
        Long createdAt,
        Long updatedAt
) {

    public record UserInfo(
            String id,
            String name,
            String avatar
    ) {
    }

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                new ReviewResponse.UserInfo(
                        review.user.id,
                        review.user.name,
                        review.user.avatar
                ),
                review.id,
                review.message,
                review.rating,
                review.createdAt.getTime(),
                review.updatedAt != null ? review.updatedAt.getTime() : null
        );
    }
}
