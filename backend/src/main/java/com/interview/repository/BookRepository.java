package com.interview.repository;

import com.interview.repository.model.Book;
import com.interview.repository.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, UUID id);

    Optional<Book> findByIsbn(String isbn);

    @Query("""
            SELECT b FROM Book b
            WHERE (:query    IS NULL OR LOWER(b.title)  LIKE LOWER(CONCAT('%', :query, '%'))
                                    OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:genre    IS NULL OR b.genre    = :genre)
              AND (:minPrice IS NULL OR b.price   >= :minPrice)
              AND (:maxPrice IS NULL OR b.price   <= :maxPrice)
            """)
    Page<Book> search(
            @Param("query")    String query,
            @Param("genre") Genre genre,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}