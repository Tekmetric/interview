package com.interview.service.dto;

import java.util.Objects;

public class ArtistDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistDTO artistDTO = (ArtistDTO) o;
        return Objects.equals(id, artistDTO.id) && Objects.equals(name, artistDTO.name) && Objects.equals(imageUrl, artistDTO.imageUrl) && Objects.equals(description, artistDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, description);
    }
}
