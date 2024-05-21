package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.BookDTO;
import com.interview.bookstore.api.dto.DetailedBookDTO;
import com.interview.bookstore.api.dto.NewBookDTO;
import com.interview.bookstore.api.dto.UpdateBookDTO;
import com.interview.bookstore.domain.Book;
import com.interview.bookstore.domain.BookDetails;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
       return new BookDTO(
               book.getId(),
               book.getTitle(),
               AuthorMapper.toDTO(book.getAuthor()),
               book.getPrice()
       );
    }

    public static DetailedBookDTO toDetailedDTO(Book book) {
        var bookDTO = new DetailedBookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(AuthorMapper.toDTO(book.getAuthor()));
        bookDTO.setPrice(book.getPrice());

        var details = book.getBookDetails();
        var reviewDTOs = book.getReviews().stream()
                        .map(ReviewMapper::toDTO)
                        .toList();

        bookDTO.setDescription(details.getDescription());
        bookDTO.setIsbn(details.getIsbn());
        bookDTO.setPublicationDate(details.getPublicationDate());
        bookDTO.setPageCount(details.getPageCount());
        bookDTO.setReviews(reviewDTOs);

        return bookDTO;
    }

    public static Book toDomain(NewBookDTO newBookDTO) {
        var book = new Book();
        book.setTitle(newBookDTO.getTitle());
        book.setBookDetails(getDetails(newBookDTO));
        book.setPrice(newBookDTO.getPrice());

        return book;
    }

    public static Book toDomain(UpdateBookDTO updateBookDTO) {
        var book = new Book();
        book.setTitle(updateBookDTO.getTitle());
        book.setPrice(updateBookDTO.getPrice());
        var bookDetails = new BookDetails();
        bookDetails.setDescription(updateBookDTO.getDescription());
        bookDetails.setPageCount(updateBookDTO.getPageCount());
        book.setBookDetails(bookDetails);

        return book;
    }

    private static BookDetails getDetails(NewBookDTO newBook) {
        var bookDetails = new BookDetails();
        bookDetails.setDescription(newBook.getDescription());
        bookDetails.setIsbn(newBook.getIsbn());
        bookDetails.setPublicationDate(newBook.getPublicationDate());
        bookDetails.setPageCount(newBook.getPageCount());

        return bookDetails;
    }
}
