package com.interview.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.interview.models.Movie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MovieDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Genre is required")
    private String genre;

    @Min(value = 0, message = "Rating must be greater than 0")
    @Max(value = 10, message = "Rating must be less than 10")
    private BigDecimal rating;

    private int releaseYear;

    @Min(value = 1, message = "Duration must be greater than 0")
    private int duration;

    @NotNull(message = "Language is required")
    private String language;

    private BigDecimal budget;

    private BigDecimal boxOffice;

    @NotNull(message = "Director is required")
    private DirectorDTO director;

    private Instant createdAt;

    private Instant updatedAt;

    private List<KeywordDTO> keywords = new ArrayList<>();

    private List<ActorDTO> actors = new ArrayList<>();

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
        this.director = new DirectorDTO(movie.getDirector());
        this.keywords = movie.getKeywords().stream().map(KeywordDTO::new).toList();
        this.actors = movie.getActors().stream().map(ActorDTO::new).toList();

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

    public void setDirector(DirectorDTO director) {
        this.director = director;
    }

    public DirectorDTO getDirector() {
        return director;
    }

    public void setUpdatedAt(Instant updated_at) {
        this.updatedAt = updated_at;
    }

    public void setKeywords(List<KeywordDTO> keywords) {
        this.keywords = keywords;
    }

    public List<KeywordDTO> getKeywords() {
        return keywords;
    }

    public void setActors(List<ActorDTO> actors) {
        this.actors = actors;
    }

    public List<ActorDTO> getActors() {
        return actors;
    }
}
