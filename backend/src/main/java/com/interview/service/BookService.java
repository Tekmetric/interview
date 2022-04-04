package com.interview.service;

import com.interview.model.Book;

import java.util.List;

public interface BookService {

    List<Book> getBooks();
    Book getBookById(Long id);
    void deleteBook(Long id);
    Book updateBook(Long id, Book book);
    Book createBook(Book book);
}
