package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.BookReviewDTO;
import com.interview.bookstore.api.dto.NewBookReviewDTO;
import com.interview.bookstore.domain.BookReview;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ReviewMapperTest {

    private static final Long REVIEW_ID = 1L;
    private static final Integer REVIEW_SCORE = 5;
    private static final String REVIEW_TEXT = "Excellent book.";

    @Test
    void mapDomainReviewToReviewDTO() {
        BookReview reviewEntity = new BookReview();
        setField(reviewEntity, "id", REVIEW_ID);
        reviewEntity.setScore(REVIEW_SCORE);
        reviewEntity.setText(REVIEW_TEXT);

        BookReviewDTO reviewDTO = ReviewMapper.toDTO(reviewEntity);

        assertThat(reviewDTO.getId()).isEqualTo(REVIEW_ID);
        assertThat(reviewDTO.getScore()).isEqualTo(REVIEW_SCORE);
        assertThat(reviewDTO.getText()).isEqualTo(REVIEW_TEXT);
    }

    @Test
    void mapNewReviewDTOToDomainReview() {
        NewBookReviewDTO reviewDTO = new NewBookReviewDTO();
        reviewDTO.setScore(REVIEW_SCORE);
        reviewDTO.setText(REVIEW_TEXT);

        BookReview reviewEntity = ReviewMapper.toDomain(reviewDTO);

        assertThat(reviewEntity.getId()).isNull();
        assertThat(reviewEntity.getScore()).isEqualTo(REVIEW_SCORE);
        assertThat(reviewEntity.getText()).isEqualTo(REVIEW_TEXT);
    }


}
