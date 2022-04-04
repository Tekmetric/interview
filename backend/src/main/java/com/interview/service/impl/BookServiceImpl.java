package com.interview.service.impl;

import com.interview.exception.BookServiceAPIException;
import com.interview.model.Book;
import com.interview.repository.BookRepository;
import com.interview.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String NO_BOOK_FOUND = "no book with id: %s found.";

    private final BookRepository bookRepository;

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookServiceAPIException(String.format(NO_BOOK_FOUND, id)));
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book currentBook = bookRepository.findById(id).orElseThrow(() -> new BookServiceAPIException(String.format(NO_BOOK_FOUND, id)));
        currentBook.setTitle(book.getTitle());
        currentBook.setIsbn(book.getIsbn());
        currentBook.setPublisher(book.getPublisher());
        currentBook.setPublicationYear(book.getPublicationYear());
        currentBook.setAuthors(book.getAuthors());
        return bookRepository.save(currentBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.findById(id).orElseThrow(() -> new BookServiceAPIException(String.format(NO_BOOK_FOUND, id)));
        bookRepository.deleteById(id);
    }
}
