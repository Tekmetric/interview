package com.interview.resource;

import com.interview.dto.BookDto;
import com.interview.dto.PageRequestDto;
import com.interview.dto.PageResponseDto;
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


}
