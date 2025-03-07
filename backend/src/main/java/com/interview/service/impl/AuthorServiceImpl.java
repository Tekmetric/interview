package com.interview.service.impl;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedAuthorsDto;
import com.interview.entity.Author;
import com.interview.repository.AuthorRepository;
import com.interview.repository.BookRepository;
import com.interview.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public PaginatedAuthorsDto getAll(String keyword, Pageable pageable) {
        logger.debug("Getting all authors from DB with keyword {} and pageable {}", keyword, pageable);
        final Page<Author> pageOfAuthors;
        if (StringUtils.hasLength(keyword)) {
            pageOfAuthors = authorRepository.findAllByKeyword(keyword.toUpperCase(), pageable);
        }
        else {
            pageOfAuthors = authorRepository.findAll(pageable);
        }
        return new PaginatedAuthorsDto(pageable.getPageNumber(),
                pageOfAuthors.getTotalPages(),
                pageOfAuthors.getTotalElements(),
                pageOfAuthors.getContent()
                        .stream()
                        .map(AuthorDto::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public AuthorDto findById(long id) {
        logger.debug("Finding author by id {}", id);
        return authorRepository.findById(id).map(AuthorDto::new).orElse(null);
    }

    @Override
    public List<BookDto> findBooksOfAuthorById(long id) {
        logger.debug("Finding books of author by id {}", id);
        return bookRepository.findByAuthorId(id).stream().map(BookDto::new).collect(Collectors.toList());
    }

    @Override
    public AuthorDto save(AuthorDto authorDto) {
        logger.debug("Saving book {}", authorDto);
        Author author = new Author(authorDto);
        authorRepository.save(author);
        return new AuthorDto(author);
    }

    @Override
    public AuthorDto update(AuthorDto authorDto) {
        logger.debug("Updating author {}", authorDto);
        return authorRepository.findById(authorDto.getId())
                .map(author -> {
                    BeanUtils.copyProperties(authorDto, author);
                    authorRepository.save(author);
                    return new AuthorDto(author);
                })
                .orElse(null);
    }

    @Override
    public void delete(long id) {
        logger.debug("Deleting author by id {}", id);
        authorRepository.deleteById(id);
    }
}
