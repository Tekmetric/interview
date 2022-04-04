package com.interview.controller;

import com.interview.dto.BookDto;
import com.interview.mapper.MapStructMapper;
import com.interview.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final MapStructMapper mapStructMapper;

    @Autowired
    public BookController(BookService bookService, MapStructMapper mapStructMapper) {
        this.bookService = bookService;
        this.mapStructMapper = mapStructMapper;
    }

    @GetMapping
    public List<BookDto> getBooks() {
        return bookService.getBooks()
                .stream()
                .map(mapStructMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable Long id) {
        return mapStructMapper.entityToDto(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto book) throws URISyntaxException {
        BookDto newBook = mapStructMapper.entityToDto(bookService.createBook(mapStructMapper.dtoToEntity(book)));
        return ResponseEntity.created(new URI("/api/v1/books/" + newBook.getId())).body(newBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto book) {
        BookDto updatedBook = mapStructMapper.entityToDto(bookService.updateBook(id, mapStructMapper.dtoToEntity(book)));
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDto> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
}
