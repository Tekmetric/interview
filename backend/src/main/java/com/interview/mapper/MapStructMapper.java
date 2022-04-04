package com.interview.mapper;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.model.Author;
import com.interview.model.Book;

public interface MapStructMapper {

    BookDto entityToDto(Book book);
    Book dtoToEntity(BookDto bookDto);
    AuthorDto entityToDto(Author author);
    Author dtoToEntity(AuthorDto authorDto);
}
