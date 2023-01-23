package com.interview.dto.user;

import com.interview.dto.ApiResponseDto;

public class TokenResponseDto extends ApiResponseDto {
    private String accessToken;

    public TokenResponseDto(String accessToken) {
        super();
        this.accessToken = accessToken;
    }

    public TokenResponseDto() {
        super();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}



