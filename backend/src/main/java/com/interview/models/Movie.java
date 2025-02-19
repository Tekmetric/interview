package com.interview.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.interview.dtos.MovieDTO;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private String genre;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "release_year")
    private int releaseYear;

    private int duration;

    @Column(name = "director_id")
    @ManyToOne(targetEntity = Director.class)
    private Director director;

    @ManyToMany
    @JoinTable(name = "movie_actor", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> actors;

    @ManyToMany
    @JoinTable(name = "movie_keyword", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keyword> keywords;

    private String language;

    private BigDecimal budget;

    @Column(name = "box_office")
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
        this.rating = movieDTO.getRating();
        this.releaseYear = movieDTO.getReleaseYear();
        this.duration = movieDTO.getDuration();
        this.language = movieDTO.getLanguage();
        this.budget = movieDTO.getBudget();
        this.boxOffice = movieDTO.getBoxOffice();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.actors = movieDTO.getActors().stream().map((actorDTO) -> new Actor(actorDTO)).collect(Collectors.toSet());
        this.keywords = movieDTO.getKeywords().stream().map((keywordDTO) -> new Keyword(keywordDTO))
                .collect(Collectors.toSet());
    }

    public long getId() {
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

    public Set<Actor> getActors() {
        return actors;
    }

    public Set<Keyword> getKeywords() {
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

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

    public void setKeywords(Set<Keyword> keywords) {
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

    public void setId(long id) {
        this.id = id;
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
