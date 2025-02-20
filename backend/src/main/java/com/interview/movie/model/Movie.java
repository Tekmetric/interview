package com.interview.movie.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.interview.actor.model.Actor;
import com.interview.director.model.Director;
import com.interview.keyword.model.Keyword;
import com.interview.movie.dto.MovieDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String genre;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "release_year")
    private int releaseYear;

    private int duration;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = true)
    private Director director;

    @ManyToMany
    @JoinTable(name = "movie_actor", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private List<Actor> actors = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "movie_keyword", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private List<Keyword> keywords = new ArrayList<>();

    private String language;

    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(precision = 10, scale = 2)
    private BigDecimal boxOffice;

    @Column(updatable = false, nullable = false, name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    public Movie() {
    }

    public Movie(MovieDTO movieDTO) {
        this.title = movieDTO.getTitle();
        this.description = movieDTO.getDescription();
        this.genre = movieDTO.getGenre();
        this.director = new Director(movieDTO.getDirector());
        this.actors = movieDTO.getActors().stream().map(Actor::new).toList();
        this.keywords = movieDTO.getKeywords().stream().map(Keyword::new).toList();
        this.rating = movieDTO.getRating();
        this.releaseYear = movieDTO.getReleaseYear();
        this.duration = movieDTO.getDuration();
        this.language = movieDTO.getLanguage();
        this.budget = movieDTO.getBudget();
        this.boxOffice = movieDTO.getBoxOffice();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
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

    public Director getDirector() {
        return director;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Keyword> getKeywords() {
        return keywords;
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

    public void setDirector(Director director) {
        this.director = director;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
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

    public void setUpdatedAt(Instant updated_at) {
        this.updatedAt = updated_at;
    }

    public void setCreatedAt(Instant created_at) {
        this.createdAt = created_at;
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

}
