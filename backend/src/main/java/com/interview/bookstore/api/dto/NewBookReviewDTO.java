package com.interview.bookstore.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Setter
@Getter
public class NewBookReviewDTO {

    @NotNull
    @Range(min = 1, max = 5)
    private Integer score;

    @Size(max = 512)
    private String text;

}
