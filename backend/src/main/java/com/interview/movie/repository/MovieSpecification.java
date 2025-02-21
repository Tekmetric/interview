package com.interview.movie.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.interview.movie.model.Movie;

import jakarta.persistence.criteria.Predicate;

public final class MovieSpecification {

    private MovieSpecification() {
    }

    public static Specification<Movie> hasGenre(String genre) {
        return (root, query, cb) -> genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Movie> hasActor(String firstName, String lastName) {
        return (root, query, cb) -> {
            if (firstName == null && lastName == null) {
                return null;
            }

            Predicate predicate = cb.conjunction();

            if (firstName != null) {
                predicate = cb.and(predicate, cb.equal(root.get("actors").get("firstName"), firstName));
            }
            if (lastName != null) {
                predicate = cb.and(predicate, cb.equal(root.get("actors").get("lastName"), lastName));
            }

            return predicate;
        };
    }

    public static Specification<Movie> hasDirector(String firstName, String lastName) {
        return (root, query, cb) -> {
            if (firstName == null && lastName == null) {
                return null;
            }

            Predicate predicate = cb.conjunction();

            if (firstName != null) {
                predicate = cb.and(predicate, cb.equal(root.get("director").get("firstName"), firstName));
            }
            if (lastName != null) {
                predicate = cb.and(predicate, cb.equal(root.get("director").get("lastName"), lastName));
            }

            return predicate;
        };
    }

    public static Specification<Movie> hasKeyword(String keyword) {
        return (root, query, cb) -> keyword == null ? null : cb.equal(root.get("keywords").get("name"), keyword);
    }

    public static Specification<Movie> hasLanguage(String language) {
        return (root, query, cb) -> language == null ? null : cb.equal(root.get("language"), language);
    }

    public static Specification<Movie> hasReleaseYear(Integer releaseYear) {
        return (root, query, cb) -> releaseYear == null ? null : cb.equal(root.get("releaseYear"), releaseYear);
    }

    public static Specification<Movie> hasMinRating(BigDecimal minRating) {
        return (root, query, cb) -> minRating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

}
