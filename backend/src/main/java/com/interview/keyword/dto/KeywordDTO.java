package com.interview.keyword.dto;

import com.interview.keyword.model.Keyword;

import jakarta.validation.constraints.NotBlank;

public class KeywordDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    public KeywordDTO() {
    }

    public KeywordDTO(Keyword keyword) {
        this.name = keyword.getName();
        this.id = keyword.getId();
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
