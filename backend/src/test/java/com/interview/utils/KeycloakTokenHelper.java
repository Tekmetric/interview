package com.interview.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class KeycloakTokenHelper {

    private static final String TEST_USERNAME = "user";
    private static final String TEST_PASSWORD = "user123";

    private static final WebClient webClient = WebClient.create();

    public static String getAccessToken(final String keycloakBaseUrl, final String username, final String password) {
        String tokenUrl = keycloakBaseUrl + "/realms/tekmetric/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "integration-test-client");
        formData.add("username", username);
        formData.add("password", password);
        formData.add("scope", "openid profile email");

        TokenResponse response = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        return response != null ? response.accessToken : null;
    }

    public static String getUserAccessToken(final String keycloakBaseUrl) {
        return getAccessToken(keycloakBaseUrl, TEST_USERNAME, TEST_PASSWORD);
    }

    public static String getTestUsername(){
        return TEST_USERNAME;
    }

    private static class TokenResponse {
        @JsonProperty("access_token")
        public String accessToken;

        @JsonProperty("token_type")
        public String tokenType;

        @JsonProperty("expires_in")
        public int expiresIn;
    }

}
