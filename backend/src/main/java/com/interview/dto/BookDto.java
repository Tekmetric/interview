package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("publication_year")
    private Integer publicationYear;
    @JsonProperty("publisher")
    private String publisher;
    @JsonProperty("authors")
    private Set<AuthorDto> authors;
}
