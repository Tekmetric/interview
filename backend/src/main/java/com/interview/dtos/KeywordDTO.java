package com.interview.dtos;

import com.interview.models.Keyword;

public class KeywordDTO {
    private String keyword;

    public KeywordDTO() {
    }

    public KeywordDTO(Keyword keyword) {
        this.keyword = keyword.getKeyword();
    }

    public String getKeyword() {
        return keyword;
    }
}
