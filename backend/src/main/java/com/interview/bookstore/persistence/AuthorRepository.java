package com.interview.bookstore.persistence;

import com.interview.bookstore.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> { }
