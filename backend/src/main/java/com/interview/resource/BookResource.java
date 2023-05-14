package com.interview.resource;

import com.interview.dto.*;
import com.interview.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookResource {

    private final BookService bookService;

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @GetMapping
    public PageResponseDto<BookDto> getBooks(@ModelAttribute PageRequestDto pageRequestDto) {
        return bookService.getBooks(pageRequestDto);
    }

    @PostMapping
    public IdWrapperDto createBook(@RequestBody BookDto bookDto) {
        return bookService.createBooks(bookDto);
    }

    @PatchMapping("/{id}")
    public void updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        bookService.updateBook(id, bookDto);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @PostMapping("/{id}/review")
    public void addReview(@PathVariable Long id, @RequestBody ReviewDto reviewDto) {
        bookService.addReview(id, reviewDto);
    }

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) {
        bookService.deleteReview(id);
    }

}
