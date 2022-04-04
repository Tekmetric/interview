package com.interview.mapper.impl;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.mapper.MapStructMapper;
import com.interview.model.Author;
import com.interview.model.Book;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapStructMapperImpl implements MapStructMapper {

    @Override
    public BookDto entityToDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDto bookDto = new BookDto();

        bookDto.setId( book.getId() );
        bookDto.setTitle( book.getTitle() );
        bookDto.setIsbn( book.getIsbn() );
        bookDto.setPublicationYear( book.getPublicationYear() );
        bookDto.setPublisher( book.getPublisher() );
        Set<Author> set = book.getAuthors();
        if ( set != null ) {
            bookDto.setAuthors( set.stream().map(this::entityToDto).collect(Collectors.toSet()) );
        }

        return bookDto;
    }

    @Override
    public Book dtoToEntity(BookDto bookDto) {
        if ( bookDto == null ) {
            return null;
        }

        Book book = new Book();

        book.setId( bookDto.getId() );
        book.setTitle( bookDto.getTitle() );
        book.setIsbn( bookDto.getIsbn() );
        book.setPublicationYear( bookDto.getPublicationYear() );
        book.setPublisher( bookDto.getPublisher() );
        Set<AuthorDto> set = bookDto.getAuthors();
        if ( set != null ) {
            book.setAuthors( set.stream().map(this::dtoToEntity).collect(Collectors.toSet()) );
        }

        return book;
    }

    @Override
    public AuthorDto entityToDto(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorDto authorDto = new AuthorDto();

        authorDto.setId( author.getId() );
        authorDto.setName( author.getName() );

        return authorDto;
    }

    @Override
    public Author dtoToEntity(AuthorDto authorDto) {
        if ( authorDto == null ) {
            return null;
        }

        Author author = new Author();

        author.setId( authorDto.getId() );
        author.setName( authorDto.getName() );

        return author;
    }
}
