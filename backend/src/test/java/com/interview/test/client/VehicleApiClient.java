package com.interview.test.client;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.NotFoundException;
import com.interview.exception.ValidationException;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VehicleApiClient {

	private static final String API = "/api/vehicles";

	private final TestRestTemplate restClient;

	public VehicleApiClient(TestRestTemplate restClient) {
		this.restClient = restClient;
	}

	public List<VehicleResponse> search() {
		ResponseEntity<VehicleResponse[]> response = restClient.getForEntity(
				API, VehicleResponse[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		return Arrays.asList(response.getBody());
	}

	public VehicleResponse create(VehicleRequest request) {
		ResponseEntity<VehicleResponse> response = restClient.postForEntity(
				API, request, VehicleResponse.class);

		if (HttpStatus.BAD_REQUEST == response.getStatusCode()) {
			throw new ValidationException("");
		}

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		return response.getBody();
	}

	public VehicleResponse get(UUID id) {
		ResponseEntity<VehicleResponse> response = restClient.getForEntity(
				API + "/" + id, VehicleResponse.class);

		if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
			throw new NotFoundException("");
		}

		assertEquals(HttpStatus.OK, response.getStatusCode());
		return response.getBody();
	}

	public VehicleResponse update(UUID id, VehicleRequest request) {
		ResponseEntity<VehicleResponse> response = restClient.exchange(
				API + "/" + id, HttpMethod.PUT,
				new HttpEntity<>(request), VehicleResponse.class);

		if (HttpStatus.NOT_FOUND == response.getStatusCode()) {
			throw new NotFoundException("");
		}

		assertEquals(HttpStatus.OK, response.getStatusCode());
		return response.getBody();
	}

	public void delete(UUID id) {
		ResponseEntity<Void> response = restClient.exchange(
				API + "/" + id, HttpMethod.DELETE, null, Void.class);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}
