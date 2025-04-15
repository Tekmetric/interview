package com.interview.functional;

import com.interview.dto.RepairServiceDTO;
import com.interview.dto.RepairServiceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import com.interview.configuration.TestSecurityConfig;

/**
 * End-to-end functional tests for RepairServiceController.
 * These tests start a real server and send HTTP requests to test the full stack.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestSecurityConfig.class})
@ActiveProfiles("test")
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RepairServiceControllerFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/repair-services";
    }

    /**
     * Test creating a new repair service (happy path).
     */
    @Test
    void testCreateRepairService() {
        // Arrange
        var newService = RepairServiceDTO.builder()
                .customerName("John Doe")
                .customerPhone("5551234567")
                .vehicleMake("Toyota")
                .vehicleModel("Camry")
                .vehicleYear(2020)
                .licensePlate("ABC123")
                .serviceDescription("Oil change and tire rotation")
                .odometerReading(35000)
                .status(RepairServiceStatus.PENDING)
                .build();

        // Act
        var requestEntity = new HttpEntity<>(newService, getJsonHeaders());
        var response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Assert
        assertSame(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
    }

    /**
     * Test getting a repair service by ID (happy path).
     */
    @Test
    void testGetRepairServiceById() throws IOException {
        // Arrange
        var newService = RepairServiceDTO.builder()
                .customerName("Test Customer")
                .customerPhone("5551234567")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription("Test service description")
                .odometerReading(25000)
                .status(RepairServiceStatus.PENDING)
                .build();

        // Create the service
        var createEntity = new HttpEntity<>(newService);
        var createResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                createEntity,
                String.class
        );
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        var responseBody = createResponse.getBody();
        var rootNode = objectMapper.readTree(responseBody);
        var dataNode = rootNode.get("data");
        long serviceId = dataNode.get("id").asLong();

        // Act - Get the service by ID
        var response = restTemplate.exchange(
                getBaseUrl() + "/" + serviceId,
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
    }

    @Test
    void testGetAllRepairServices() {
        // Act - Get all services with pagination
        var response = restTemplate.exchange(
                getBaseUrl() + "?pageNumber=0&pageSize=10&sortBy=id&sortDirection=desc",
                HttpMethod.GET,
                new HttpEntity<>(getJsonHeaders()),
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
    }

    @Test
    void testUpdateRepairService() throws IOException {
        // First create a service that we can update
        var newService = RepairServiceDTO.builder()
                .customerName("Original Name")
                .customerPhone("5551234567")
                .vehicleMake("Original Make")
                .vehicleModel("Original Model")
                .vehicleYear(2020)
                .licensePlate("ORG123")
                .serviceDescription("Original description")
                .odometerReading(30000)
                .status(RepairServiceStatus.PENDING)
                .build();

        // Create the service first
        var createEntity = new HttpEntity<>(newService, getJsonHeaders());
        var createResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                createEntity,
                String.class
        );
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        var responseBody = createResponse.getBody();
        var rootNode = objectMapper.readTree(responseBody);
        var dataNode = rootNode.get("data");
        long serviceId = dataNode.get("id").asLong();
        
        // Create an update DTO
        var updateService = RepairServiceDTO.builder()
                .id(serviceId)
                .customerName("Updated Customer")
                .customerPhone("5559998888")
                .vehicleMake("Updated Make")
                .vehicleModel("Updated Model")
                .vehicleYear(2023)
                .licensePlate("UPD123")
                .serviceDescription("Updated service description")
                .odometerReading(50000)
                .status(RepairServiceStatus.IN_PROGRESS)
                .build();

        // Act - Update the service
        var updateEntity = new HttpEntity<>(updateService, getJsonHeaders());
        var response = restTemplate.exchange(
                getBaseUrl() + "/" + serviceId,
                HttpMethod.PUT,
                updateEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
        assertTrue(response.getBody().contains("Updated Customer"));
        assertTrue(response.getBody().contains("IN_PROGRESS"));
    }

    @Test
    void testDeleteRepairService() throws IOException {
        // First create a service that we can delete
        var newService = RepairServiceDTO.builder()
                .customerName("Delete Test")
                .customerPhone("5551234567")
                .vehicleMake("Test")
                .vehicleModel("Delete")
                .vehicleYear(2025)
                .licensePlate("DEL123")
                .serviceDescription("Service to be deleted")
                .odometerReading(10000)
                .status(RepairServiceStatus.PENDING)
                .build();

        // Create the service
        var createEntity = new HttpEntity<>(newService, getJsonHeaders());
        var createResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                createEntity,
                String.class
        );
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        var responseBody = createResponse.getBody();
        var rootNode = objectMapper.readTree(responseBody);
        var dataNode = rootNode.get("data");
        long serviceId = dataNode.get("id").asLong();
            
        // Act - Delete the service
        var response = restTemplate.exchange(
                getBaseUrl() + "/" + serviceId,
                HttpMethod.DELETE,
                new HttpEntity<>(getJsonHeaders()),
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
            
        // Verify the service is deleted by trying to get it
        var getResponse = restTemplate.exchange(
                getBaseUrl() + "/" + serviceId,
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    private HttpHeaders getJsonHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
