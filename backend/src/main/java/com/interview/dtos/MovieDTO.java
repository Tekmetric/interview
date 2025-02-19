package com.interview.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.interview.models.Movie;

public class MovieDTO {
    private String title;
    private String description;
    private String genre;
    private BigDecimal rating;
    private int releaseYear;
    private int duration;
    private int directorId;
    private String language;
    private BigDecimal budget;
    private BigDecimal boxOffice;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<ActorDTO> actors;
    private Set<KeywordDTO> keywords;

    public MovieDTO() {
    }

    public MovieDTO(Movie movie) {
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.genre = movie.getGenre();
        this.rating = movie.getRating();
        this.releaseYear = movie.getReleaseYear();
        this.duration = movie.getDuration();
        this.directorId = movie.getDirector().getId();
        this.language = movie.getLanguage();
        this.budget = movie.getBudget();
        this.boxOffice = movie.getBoxOffice();
        this.createdAt = movie.getCreatedAt();
        this.updatedAt = movie.getUpdatedAt();
        this.actors = movie.getActors().stream().map((actor) -> new ActorDTO(actor)).collect(Collectors.toSet());
        this.keywords = movie.getKeywords().stream().map((keyword) -> new KeywordDTO(keyword))
                .collect(Collectors.toSet());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDuration() {
        return duration;
    }

    public int getDirectorId() {
        return directorId;
    }

    public String getLanguage() {
        return language;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getBoxOffice() {
        return boxOffice;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<ActorDTO> getActors() {
        return actors;
    }

    public Set<KeywordDTO> getKeywords() {
        return keywords;
    }

}
