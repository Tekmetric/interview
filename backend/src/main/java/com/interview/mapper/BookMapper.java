package com.interview.mapper;

import com.interview.domain.Book;
import com.interview.domain.Review;
import com.interview.dto.BookDto;
import com.interview.dto.PageResponseDto;
import com.interview.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookMapper {

    private final ReviewMapper reviewMapper;

    public BookDto toDto(Book book) {
        List<ReviewDto> reviews = book.getReviews().stream().map(reviewMapper::toDto).collect(Collectors.toList());
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .reviews(reviews)
                .build();
    }

    public PageResponseDto<BookDto> toDto(Page<Book> books) {
        List<BookDto> bookDtos = books.getContent().stream()
                .map(this::toDtoWithoutReviews)
                .collect(Collectors.toList());
        return new PageResponseDto<>(bookDtos, books.getNumber(), books.getNumberOfElements(), books.getTotalElements());
    }

    private BookDto toDtoWithoutReviews(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }

    public Book toEntity(BookDto bookDto) {
        Book book = Book.builder()
                .author(bookDto.getAuthor())
                .title(bookDto.getTitle())
                .build();

        if (!CollectionUtils.isEmpty(bookDto.getReviews())) {
            List<Review> reviews = bookDto.getReviews().stream()
                    .map(reviewMapper::toEntity)
                    .collect(Collectors.toList());
            book.addReviews(reviews);
        }

        return book;
    }
}
