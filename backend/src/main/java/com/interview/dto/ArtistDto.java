package com.interview.dto;

import javax.validation.constraints.NotBlank;

public class ArtistDto {

    private Long id;

    @NotBlank(message = "Artist name is required")
    private String name;

    public ArtistDto() {
    }

    public ArtistDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
