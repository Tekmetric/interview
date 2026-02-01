package com.interview.dto;

import java.time.LocalDate;

public class SearchResultDto {

    private Long id;
    private String entityType;
    private String name;
    private String artistName;
    private LocalDate releaseDate;

    public SearchResultDto() {
    }

    public SearchResultDto(Long id, String entityType, String name, String artistName, LocalDate releaseDate) {
        this.id = id;
        this.entityType = entityType;
        this.name = name;
        this.artistName = artistName;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
