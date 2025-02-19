package com.interview.dtos;

import com.interview.models.Keyword;

public class KeywordDTO {
    private String name;

    public KeywordDTO() {
    }

    public KeywordDTO(Keyword keyword) {
        this.name = keyword.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
