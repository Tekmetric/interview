package com.interview.bookstore.persistence;

import com.interview.bookstore.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
        select b from Book b join fetch b.author
    """)
    List<Book> findAll();

    @Query("""
        select b from Book b
            join fetch b.author
            join fetch b.bookDetails
            left join fetch b.reviews
        where b.id = :id
    """)
    Optional<Book> findDetailedById(@Param("id") Long id);

    @Query("""
        select bd.id from BookDetails bd
        where bd.isbn = :isbn
    """)
    Optional<Long> findBookIdByIsbn(@Param("isbn") String isbn);

}
