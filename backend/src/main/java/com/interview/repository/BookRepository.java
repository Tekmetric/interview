package com.interview.repository;

import com.interview.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b inner join b.author a " +
            "WHERE :keyword IS NULL " +
            "OR UPPER(b.name) LIKE %:keyword% " +
            "OR UPPER(a.firstName) LIKE %:keyword% " +
            "OR UPPER(a.lastName) LIKE %:keyword% ")
    Page<Book> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
    List<Book> findByAuthorId(Long id);
}
