package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotNull
    @Size(max = 100, message = "Title max size is 100 characters.")
    private String title;

    @NotNull
    @Size(max = 50, message = "Author max size is 50 characters")
    private String author;

    @Valid
    private List<ReviewDto> reviews;
}
