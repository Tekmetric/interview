package com.interview.dto.user;

import com.interview.dto.ApiResponseDto;

public class AuthResponseDto extends ApiResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponseDto(boolean success, String message, String accessToken, String tokenType) {
        super(success, message);
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public AuthResponseDto(boolean success, String message) {
        super(success, message);
    }

    public AuthResponseDto() {
        super();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
