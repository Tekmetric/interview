package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.AuthorDTO;
import com.interview.bookstore.domain.Author;

public class AuthorMapper {

    public static AuthorDTO toDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getFirstName(), author.getLastName());
    }

}
