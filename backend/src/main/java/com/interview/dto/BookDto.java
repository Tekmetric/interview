package com.interview.dto;

import com.interview.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class BookDto implements Serializable {
    private Long id;

    @NotEmpty(message = "{error.required.book.name}")
    private String name;

    @NotNull(message = "{error.required.book.author}")
    private AuthorDto author;

    @Min(value = 1000, message = "{error.minValue.book.publicationYear}")
    private Integer publicationYear;

    public BookDto(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        if (book.getAuthor() != null) {
            this.author = new AuthorDto(book.getAuthor());
        }
        this.publicationYear = book.getPublicationYear();
    }

    public BookDto(long id) {
        this.id = id;
    }
}
