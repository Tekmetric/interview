package com.interview.repository;

import com.interview.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews WHERE b.id = :id")
    Optional<Book> findByIdWithReviews(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Book b where b.id = :id")
    void deleteById(Long id);

    boolean existsByTitleAndAuthor(String title, String author);

    boolean existsByTitleAndAuthorAndIdNot(String title, String author, Long id);

}
