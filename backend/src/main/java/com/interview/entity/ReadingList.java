package com.interview.entity;

import com.interview.dto.ReadingListDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_readinglist")
public class ReadingList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private User owner;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    private boolean shared;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "t_readinglist_book",
            joinColumns = @JoinColumn(name = "readinglist_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> bookList = new ArrayList<>();

    public ReadingList(ReadingListDto readingListDto) {
        if (readingListDto.getId() != null) {
            this.id = readingListDto.getId();
        }
        this.name = readingListDto.getName();
        this.owner = new  User(readingListDto.getOwner());
        this.lastUpdate = readingListDto.getLastUpdate();
        this.shared = readingListDto.isShared();
        this.bookList = readingListDto.getBooks()
                .stream()
                .map(book -> new Book(book.getId()))
                .collect(Collectors.toList());
    }

    public void addBook(Book book) {
        if (!this.bookList.contains(book)) {
            this.bookList.add(book);
        }
    }

    public void removeBook(Book book) {
        this.bookList.remove(book);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReadingList readingList = (ReadingList) o;
        return id == readingList.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
