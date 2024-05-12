package com.interview.bookstore.persistence;

import com.interview.bookstore.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
        select b from Book b
            join fetch b.bookDetails
            join fetch b.reviews
        where b.id = :id
    """)
    Optional<Book> findDetailedById(@Param("id") Long id);

    boolean existsBookByBookDetailsIsbn(String isbn);
}
