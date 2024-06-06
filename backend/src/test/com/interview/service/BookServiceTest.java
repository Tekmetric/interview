package com.interview.service;

import com.interview.dao.BookRepository;
import com.interview.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.annotation.Resource;
import java.util.List;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createBook() {
        Book book = Book.builder()
                .id(1)
                .name("Soccer tricks")
                .author("David Beckham")
                .price(13.99F)
                .build();
        bookRepository.save(book);
        Assertions.assertThat(book.getId()).isGreaterThan(0);
    }


    @Test
    @Order(2)
    public void getBookById() {
        Book book = bookRepository.findById(1).orElse(null);
        Assertions.assertThat(book.getId()).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void getAllBooks() {
        List<Book> books = bookRepository.findAll();
        Assertions.assertThat(books.size()).isGreaterThan(0);
    }


    @Test
    @Order(4)
    @Rollback(value = false)
    public void updateBook() {
        Book book = bookRepository.findById(1).orElse(null);
        book.setName("Basketball tricks");
        Book updatedBook =  bookRepository.save(book);
        Assertions.assertThat(updatedBook.getName()).isEqualTo("Basketball tricks");
    }

    @Test
    @Order(5)
    public void deleteBook() {
        bookRepository.deleteById(1);
        Book book = bookRepository.findById(1).orElse(null);
        Assertions.assertThat(book).isNull();
    }

}