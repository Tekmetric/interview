package com.interview;

import com.interview.model.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreate() {
        CustomerDTO newResource = new CustomerDTO("news@cnbsc.com", "Mary Cooper", "34 SE Highway, Holliston, MA");
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/customer", newResource, CustomerDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    public void testCreateFailDuplicateEmail() {
        CustomerDTO customer1 = new CustomerDTO("mekanic@auto.com", "Bob Cooper", "34 SE Highway, Holliston, MA");
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/customer", customer1, CustomerDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        response = restTemplate.postForEntity("http://localhost:" + port + "/customer", customer1, CustomerDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
