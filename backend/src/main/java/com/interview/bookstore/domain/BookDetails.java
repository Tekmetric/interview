package com.interview.bookstore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
public class BookDetails {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Book book;

    @Column(unique = true, nullable = false, updatable = false)
    private String isbn;

    @Column(length = 1000)
    private String description;

    @Column(name="publication_date", nullable = false, updatable = false)
    private LocalDate publicationDate;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

}
