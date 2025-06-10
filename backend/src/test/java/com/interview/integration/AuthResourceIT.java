package com.interview.integration;

import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class AuthResourceIT {

  private static final String BASE_URL = "http://localhost:8080";
  private final RestTemplate restTemplate = new RestTemplate();

  @BeforeEach
  void setUp() {
    restTemplate.setErrorHandler(
        new DefaultResponseErrorHandler() {
          @Override
          public void handleError(final ClientHttpResponse response) {
            // Do nothing, so RestTemplate does not throw exceptions for 4xx/5xx
          }
        });
  }

  @Test
  void accessProtectedEndpoint_withoutToken_shouldReturn403() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    final ResponseEntity<Void> response =
        restTemplate.exchange(
            BASE_URL + "/owners?page=0&size=1", HttpMethod.GET, request, Void.class);

    Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void authenticate_withWrongCredentials_shouldReturn401() {
    final String loginUrl = BASE_URL + "/auth/login";
    final Map<String, String> loginRequest =
        Map.of(
            "username", "wronguser",
            "password", "wrongpass");

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

    final ResponseEntity<Void> response = restTemplate.postForEntity(loginUrl, request, Void.class);

    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void accessWriteEndpoint_withReadOnlyUser_shouldReturn403() {
    // Authenticate as a user with only READ authority
    final String loginUrl = BASE_URL + "/auth/login";
    final Map<String, String> loginRequest =
        Map.of(
            "username", "guest",
            "password", "guest");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

    final ResponseEntity<String> response =
        restTemplate.postForEntity(loginUrl, request, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    final String token = JsonPath.read(response.getBody(), "$.token");

    // Try to create an owner (WRITE endpoint)
    final HttpHeaders writeHeaders = new HttpHeaders();
    writeHeaders.setContentType(MediaType.APPLICATION_JSON);
    writeHeaders.setBearerAuth(token);
    final Map<String, Object> ownerRequest =
        Map.of(
            "name", "No Write",
            "personalNumber", "999999999",
            "address", "No Addr",
            "birthDate", Instant.parse("2000-01-01T00:00:00Z"));
    final HttpEntity<Map<String, Object>> writeRequest =
        new HttpEntity<>(ownerRequest, writeHeaders);

    final ResponseEntity<String> writeResponse =
        restTemplate.postForEntity(BASE_URL + "/owners", writeRequest, String.class);

    Assertions.assertEquals(HttpStatus.FORBIDDEN, writeResponse.getStatusCode());
  }
}
