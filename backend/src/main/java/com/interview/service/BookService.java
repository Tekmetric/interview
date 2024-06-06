package com.interview.service;

import com.interview.dao.BookRepository;
import com.interview.entity.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public void createBook(Book book) {
        bookRepository.save(book);
        log.info("Book " + book.getName() + " is saved");
    }

    public Book getBookById(int id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book book = null;
        if (optionalBook.isPresent()) {
            book = optionalBook.get();
        } else {
            throw new RuntimeException("Book not found for id: " + id);
        }
        return book;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public Book updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId()).orElse(null);
        if (existingBook != null) {
            existingBook.setName(book.getName());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPrice(book.getPrice());
            bookRepository.save(existingBook);
        } else {
            throw new RuntimeException("Book not found for id: " + book.getId());
        }
        log.info("Book " + book.getName() + " has been updated");
        return existingBook;
    }

    public String deleteBookById(int id) {
        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook != null) {
            bookRepository.deleteById(id);
        } else {
            throw new RuntimeException("Book not found for id: " + id);
        }
        log.info("Book " + id + " is deleted");
        return "Book deleted";

    }

}
