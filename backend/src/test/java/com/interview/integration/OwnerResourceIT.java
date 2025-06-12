package com.interview.integration;

import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OwnerResourceIT {

  private static final String BASE_URL = "http://localhost:8080";
  private final RestTemplate restTemplate = new RestTemplate();

  private String jwtToken;

  @BeforeAll
  void authenticate() {
    final String loginUrl = BASE_URL + "/auth/login";

    final Map<String, String> loginRequest =
        Map.of(
            "username", "sorin",
            "password", "pass123");

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

    final ResponseEntity<String> response =
        restTemplate.postForEntity(loginUrl, request, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    jwtToken = JsonPath.read(response.getBody(), "$.token");
    Assertions.assertNotNull(jwtToken);
  }

  @Test
  void createOwner_andGetOwnerById() {

    final Instant birthDate = Instant.parse("1990-01-01T00:00:00Z");
    // Create an owner payload
    final Map<String, Object> ownerRequest =
        Map.of(
            "name", "John Doe",
            "personalNumber", "123456789",
            "address", "123 Main St",
            "birthDate", birthDate);

    final HttpEntity<Map<String, Object>> createRequest =
        new HttpEntity<>(ownerRequest, createAuthHeaders());

    // POST /owners
    final ResponseEntity<String> createResponse =
        restTemplate.postForEntity(BASE_URL + "/owners", createRequest, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

    final Integer ownerId = JsonPath.read(createResponse.getBody(), "$.id");
    final String name = JsonPath.read(createResponse.getBody(), "$.name");
    final String address = JsonPath.read(createResponse.getBody(), "$.address");

    Assertions.assertEquals("John Doe", name);
    Assertions.assertEquals("123 Main St", address);
    Assertions.assertNotNull(ownerId);

    // GET /owners/{id}
    final HttpEntity<Void> getRequest = new HttpEntity<>(createAuthHeaders());

    final ResponseEntity<String> getResponse =
        restTemplate.exchange(
            BASE_URL + "/owners/" + ownerId, HttpMethod.GET, getRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    Assertions.assertEquals("John Doe", JsonPath.read(getResponse.getBody(), "$.name"));
    Assertions.assertEquals("123 Main St", JsonPath.read(getResponse.getBody(), "$.address"));
    Assertions.assertEquals(
        birthDate.toString(), JsonPath.read(getResponse.getBody(), "$.birthDate"));
  }

  @Test
  void createMultipleOwners_andQueryByXseries() {
    final Instant birthDate = Instant.parse("1985-05-05T00:00:00Z");

    // Owner 1: Has Xseries cars
    final Map<String, Object> owner1Request =
        Map.of(
            "name", "Owner One",
            "personalNumber", "111111111",
            "address", "Addr 1",
            "birthDate", birthDate);
    final Integer owner1Id = createOwnerAndReturnId(owner1Request);
    addCarToOwner(owner1Id, "X1 Xseries M");
    addCarToOwner(owner1Id, "3 Sedan");

    // Owner 2: Has Xseries cars
    final Map<String, Object> owner2Request =
        Map.of(
            "name", "Owner Two",
            "personalNumber", "222222222",
            "address", "Addr 2",
            "birthDate", birthDate);
    final Integer owner2Id = createOwnerAndReturnId(owner2Request);
    addCarToOwner(owner2Id, "X3 Xseries Normal");
    addCarToOwner(owner2Id, "5 Sedan");

    // Owner 3: No Xseries cars
    final Map<String, Object> owner3Request =
        Map.of(
            "name", "Owner Three",
            "personalNumber", "333333333",
            "address", "Addr 3",
            "birthDate", birthDate);
    final Integer owner3Id = createOwnerAndReturnId(owner3Request);
    addCarToOwner(owner3Id, "3 Sedan");
    addCarToOwner(owner3Id, "5 Sedan");

    // Query for owners with "Xseries" in their cars
    final HttpEntity<Void> getRequest = new HttpEntity<>(createAuthHeaders());
    final ResponseEntity<String> getOwnersResponse =
        restTemplate.exchange(
            BASE_URL + "/owners?page=0&size=10&query=Xseries",
            HttpMethod.GET,
            getRequest,
            String.class);
    Assertions.assertEquals(HttpStatus.OK, getOwnersResponse.getStatusCode());

    // Assert only 2 owners are returned
    final List<?> owners = JsonPath.read(getOwnersResponse.getBody(), "$.content");
    Assertions.assertEquals(2, owners.size());

    // Assert each owner has at least one car with "Xseries" in the model
    for (Object owner : owners) {
      final List<String> carModels = JsonPath.read(owner, "$.cars[*].model");
      Assertions.assertTrue(carModels.stream().anyMatch(m -> m.contains("Xseries")));
    }
  }

  // Helper methods
  private HttpHeaders createAuthHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(jwtToken);
    return headers;
  }

  private Integer createOwnerAndReturnId(final Map<String, Object> ownerRequest) {
    final HttpEntity<Map<String, Object>> createRequest =
        new HttpEntity<>(ownerRequest, createAuthHeaders());
    final ResponseEntity<String> createResponse =
        restTemplate.postForEntity(BASE_URL + "/owners", createRequest, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    return JsonPath.read(createResponse.getBody(), "$.id");
  }

  private void addCarToOwner(final Integer ownerId, final String model) {
    final Map<String, Object> carRequest =
        Map.of(
            "model", model,
            "vin", "VIN" + UUID.randomUUID().toString().replace("-", "").substring(0, 6),
            "ownerId", ownerId);
    final HttpEntity<Map<String, Object>> carEntity =
        new HttpEntity<>(carRequest, createAuthHeaders());
    final ResponseEntity<String> carResponse =
        restTemplate.postForEntity(BASE_URL + "/cars", carEntity, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, carResponse.getStatusCode());
  }
}
