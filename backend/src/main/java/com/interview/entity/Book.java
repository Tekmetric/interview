package com.interview.entity;

import com.interview.dto.BookDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "t_book")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Author author;

    private Integer publicationYear;

    public Book(BookDto bookDto) {
        if (bookDto.getId() != null) {
            this.id = bookDto.getId();
        }
        this.name = bookDto.getName();
        this.author = new Author(bookDto.getAuthor());
        this.publicationYear = bookDto.getPublicationYear();
    }

    public Book(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
