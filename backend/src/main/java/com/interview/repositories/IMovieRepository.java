package com.interview.repositories;

import com.interview.models.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByGenre(String genre, Pageable pageable);

    // @Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.firstName = ?1 AND
    // a.lastName = ?2")
    Page<Movie> findByActorFirstNameLastName(String firstName, String lastName, Pageable pageable);

    Page<Movie> findByKeywords(String keyword, Pageable pageable);

    Page<Movie> findByLanguage(String language, Pageable pageable);

    Page<Movie> findByDirector(String director, Pageable pageable);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    Page<Movie> findByRating(double rating, Pageable pageable);

}
