package com.interview.resource;

import java.util.List;

public class BookListResponse extends Response {
    private List<Book> books;

    public BookListResponse(List<Book> books, String error, int responseCode) {
        super(error, responseCode);
        this.books = books;
    }

    public BookListResponse(String error, int responseCode) {
        super(error, responseCode);
    }

    public List<Book> getBooks() {
        return this.books;
    }
}
