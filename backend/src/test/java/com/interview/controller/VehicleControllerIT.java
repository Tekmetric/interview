package com.interview.controller;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.NotFoundException;
import com.interview.exception.ValidationException;
import com.interview.test.client.VehicleApiClient;
import com.interview.test.data.TestData;
import com.interview.test.data.VehicleTestData;
import com.interview.test.validation.VehicleValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(VehicleControllerIT.ClientConfiguration.class)
class VehicleControllerIT {

    @TestConfiguration
    public static class ClientConfiguration {

        @Bean
        public VehicleApiClient client(TestRestTemplate restClient) {
            return new VehicleApiClient(restClient);
        }
    }

    @Autowired
    private VehicleApiClient client;

    @Test
    void shouldCreate() {
        // given
        VehicleRequest createRequest = VehicleTestData.vehicleRequest();

        // then
        VehicleResponse createResponse = client.create(createRequest);

        VehicleValidator.validateGeneratedFields(createResponse);
        assertEquals(VehicleTestData.BRAND, createResponse.getBrand());
        assertEquals(VehicleTestData.MODEL, createResponse.getModel());
        assertEquals(VehicleTestData.YEAR, createResponse.getMadeYear());
        assertEquals(VehicleTestData.COLOR, createResponse.getColor());

        // cleanup
        client.delete(createResponse.getId());
    }

    @Test
    void shouldThrowValidationExceptionWhenCreateWithInvalidData() {
        // given
        VehicleRequest createRequest = VehicleTestData.vehicleRequest();
        createRequest.setBrand(TestData.LONG_STRING);

        assertThrowsExactly(
                ValidationException.class,
                () -> client.create(createRequest));
    }

    @Test
    void shouldGet() {
        // given
        VehicleRequest createRequest = VehicleTestData.vehicleRequest();
        VehicleResponse createResponse = client.create(createRequest);

        // then
        VehicleResponse getResponse = client.get(createResponse.getId());
        assertEquals(createResponse, getResponse);

        // cleanup
        client.delete(createResponse.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGetWithUnknownId() {
        assertThrowsExactly(
                NotFoundException.class,
                () -> client.get(UUID.randomUUID()));
    }

    @Test
    void shouldDeleteUser() {
        // given
        VehicleRequest createRequest = VehicleTestData.vehicleRequest();
        VehicleResponse createResponse = client.create(createRequest);

        // then
        client.delete(createResponse.getId());
        assertThrowsExactly(
                NotFoundException.class,
                () -> client.get(createResponse.getId()));
    }
}
