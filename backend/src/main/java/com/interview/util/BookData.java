package com.interview.util;

import com.interview.model.Book;

import java.util.Arrays;
import java.util.List;

public class BookData {

    public static List<Book> getBooks() {
        return Arrays.asList(
                new Book(1L, "Book 1", "Author 1"),
                new Book(2L, "Book 2", "Author 2"),
                new Book(3L, "Book 3", "Author 3"),
                new Book(4L, "Book 4", "Author 4"),
                new Book(5L, "Book 5", "Author 5")
        );
    }
}
