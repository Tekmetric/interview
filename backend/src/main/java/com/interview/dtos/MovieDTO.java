package com.interview.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.interview.models.Actor;
import com.interview.models.Director;
import com.interview.models.Keyword;
import com.interview.models.Movie;

public class MovieDTO {
    private String title;
    private String description;
    private String genre;
    private BigDecimal rating;
    private int releaseYear;
    private int duration;
    private String language;
    private BigDecimal budget;
    private BigDecimal boxOffice;
    private Director director;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Keyword> keywords = List.of();
    private List<Actor> actors = List.of();

    public MovieDTO() {
    }

    public MovieDTO(Movie movie) {
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.genre = movie.getGenre();
        this.rating = movie.getRating();
        this.releaseYear = movie.getReleaseYear();
        this.duration = movie.getDuration();
        this.language = movie.getLanguage();
        this.budget = movie.getBudget();
        this.boxOffice = movie.getBoxOffice();
        this.createdAt = movie.getCreatedAt();
        this.updatedAt = movie.getUpdatedAt();
        this.director = movie.getDirector();
        this.keywords = movie.getKeywords();
        this.actors = movie.getActors();

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public void setBoxOffice(BigDecimal boxOffice) {
        this.boxOffice = boxOffice;
    }

    public void setCreatedAt(Instant created_at) {
        this.createdAt = created_at;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Director getDirector() {
        return director;
    }

    public void setUpdatedAt(Instant updated_at) {
        this.updatedAt = updated_at;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Actor> getActors() {
        return actors;
    }
}
