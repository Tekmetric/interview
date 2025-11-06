package com.interview.repair_order.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview._infrastructure.domain.model.CustomError;
import com.interview.repair_order.api.model.RepairOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RepairOrderComponentTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRepairOrders_usingMockMvc() throws Exception {
        mockMvc.perform(get("/api/v0/repair-orders")
                        .header("X-AUTH-KEY", "super-secret-demo-key"))
                .andExpect(status().isOk());
    }

    @Test
    void createAndReadRepairOrder_usingMockMvc() throws Exception {

        String request = "{  \"shopId\": \"d57e391e-d296-43cd-9455-1234abcd1234\",\n" +
                "            \"externalRO\": \"Mavis-789\",\n" +
                "            \"status\": \"ESTIMATE\",\n" +
                "            \"odometerIn\": 4000,\n" +
                "            \"odometerOut\": 4100,\n" +
                "            \"notes\": \"First RO\"\n" +
                "        }";

        MvcResult result = mockMvc.perform(post("/api/v0/repair-orders")
                        .header("X-AUTH-KEY", "super-secret-demo-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");

        assertNotNull(location);
        mockMvc.perform(get(location)
                        .header("X-AUTH-KEY", "super-secret-demo-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalRO").value("Mavis-789"));
    }

    @Test
    void get_usingTestRestTemplate() throws JsonProcessingException {

        HttpEntity<Object> request = createRequestWithValidHeaders();

        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders/0a7fc60a-46cf-4238-9ae4-db68be4ef234",
                        HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RepairOrderResponse response1 = objectMapper.readValue(response.getBody(), RepairOrderResponse.class);

        assertEquals("0a7fc60a-46cf-4238-9ae4-db68be4ef234", response1.getId());
    }

    @Test
    void get_notFound_testRestTemplate() throws JsonProcessingException {
        HttpEntity<Object> request = createRequestWithValidHeaders();
        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders/bad-uuid-for-repair-order",
                        HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        CustomError error = objectMapper.readValue(response.getBody(), CustomError.class);
        assertNotNull(error.getTimeStamp());
        assertEquals("Not Found", error.getError());
        assertEquals("A repair order with ID: bad-uuid-for-repair-order cannot be found.", error.getMessage());
        assertEquals("/api/v0/repair-orders/bad-uuid-for-repair-order", error.getPath());
    }

    @Test
    void get_noCredentials() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders",
                        HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        CustomError error = objectMapper.readValue(response.getBody(), CustomError.class);
        assertNotNull(error.getTimeStamp());
        assertEquals("Unauthorized", error.getError());
        assertEquals("You do not have access to this resource!", error.getMessage());
        assertEquals("/api/v0/repair-orders", error.getPath());
    }

    @Test
    void get_badCredentials() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-AUTH-KEY", "incorrect-key");
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders",
                        HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        CustomError error = objectMapper.readValue(response.getBody(), CustomError.class);
        assertNotNull(error.getTimeStamp());
        assertEquals("Unauthorized", error.getError());
        assertEquals("You do not have access to this resource!", error.getMessage());
        assertEquals("/api/v0/repair-orders", error.getPath());
    }


    @Test
    void update_badEnum() throws JsonProcessingException {

        String requestBody = "{  \"shopId\": \"d57e391e-d296-43cd-9455-1234abcd1234\",\n" +
                "            \"externalRO\": \"Mavis-789\",\n" +
                "            \"status\": \"BAD_ENUM_VALUE\",\n" +
                "            \"odometerIn\": 4000,\n" +
                "            \"odometerOut\": 4100,\n" +
                "            \"notes\": \"First RO\"\n" +
                "        }";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-AUTH-KEY", "super-secret-demo-key");
        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders/0a7fc60a-46cf-4238-9ae4-db68be4ef234",
                        HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        CustomError response1 = objectMapper.readValue(response.getBody(), CustomError.class);
        assertNotNull(response1);

        assertEquals("Invalid value 'BAD_ENUM_VALUE' for field 'status'. Allowed values are: [ESTIMATE, " +
                "APPROVED, IN_PROGRESS, CLOSED, CANCELLED]", response1.getMessage());
        assertEquals("Bad Request", response1.getError());
        assertEquals("/api/v0/repair-orders/0a7fc60a-46cf-4238-9ae4-db68be4ef234", response1.getPath());
        assertNotNull(response1.getTimeStamp());
    }

    @Test
    void update_invalidShopId_externalRO() throws JsonProcessingException {

        String requestBody = "{  \"shopId\": \"not-a-uuid\",\n" +
                "            \"status\": \"CLOSED\",\n" +
                "            \"odometerIn\": 4000,\n" +
                "            \"odometerOut\": 4100,\n" +
                "            \"notes\": \"First RO\"\n" +
                "        }";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-AUTH-KEY", "super-secret-demo-key");
        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);


        ResponseEntity<String> response = restTemplate
                .exchange("/api/v0/repair-orders/0a7fc60a-46cf-4238-9ae4-db68be4ef234",
                        HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        CustomError response1 = objectMapper.readValue(response.getBody(), CustomError.class);
        assertNotNull(response1);

        assertEquals("externalRO: must not be null\nshopId: must be a valid UUID", response1.getMessage());
        assertEquals("Bad Request", response1.getError());
        assertEquals("/api/v0/repair-orders/0a7fc60a-46cf-4238-9ae4-db68be4ef234", response1.getPath());
        assertNotNull(response1.getTimeStamp());
    }

    private HttpEntity<Object> createRequestWithValidHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-AUTH-KEY", "super-secret-demo-key");
        return new HttpEntity<>(headers);
    }
}
