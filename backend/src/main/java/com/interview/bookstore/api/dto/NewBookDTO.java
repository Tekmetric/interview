package com.interview.bookstore.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.ISBN;

import java.time.LocalDate;

@Setter
@Getter
public class NewBookDTO {

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private Long authorId;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @ISBN
    private String isbn;

    @Size(max = 1000)
    private String description;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    @Positive
    private Integer pageCount;

}
