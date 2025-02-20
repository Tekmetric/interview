package com.interview.repositories;

import com.interview.models.Director;
import com.interview.models.Movie;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByGenre(String genre, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.actors a WHERE a.firstName = ?1 AND a.lastName = ?2")
    Page<Movie> findByActor(String firstName, String lastName, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.keywords k WHERE k.name = ?1")
    Page<Movie> findByKeyword(String keyword, Pageable pageable);

    Page<Movie> findByLanguage(String language, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.director d WHERE d.firstName = ?1 AND d.lastName = ?2")
    Page<Movie> findByDirectorFirstNameLastName(String firstName, String lastName, Pageable pageable);

    List<Movie> findByDirector(Director director);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.rating >= ?1")
    Page<Movie> findByMinRating(BigDecimal rating, Pageable pageable);

}
