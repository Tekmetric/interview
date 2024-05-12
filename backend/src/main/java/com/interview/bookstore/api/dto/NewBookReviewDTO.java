package com.interview.bookstore.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewBookReviewDTO {

    @Min(1)
    @Max(5)
    @NotNull
    private Integer score;

    @Size(max = 512)
    private String text;

}
