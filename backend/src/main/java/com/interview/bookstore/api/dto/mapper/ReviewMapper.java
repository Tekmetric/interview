package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.BookReviewDTO;
import com.interview.bookstore.api.dto.NewBookReviewDTO;
import com.interview.bookstore.domain.BookReview;

public class ReviewMapper {

    public static BookReviewDTO toDTO(BookReview review) {
        return new BookReviewDTO(review.getId(), review.getScore(), review.getText());
    }

    public static BookReview toDomain(NewBookReviewDTO newReview) {
        var domainReview = new BookReview();
        domainReview.setScore(newReview.getScore());
        domainReview.setText(newReview.getText());
        return domainReview;
    }
}
