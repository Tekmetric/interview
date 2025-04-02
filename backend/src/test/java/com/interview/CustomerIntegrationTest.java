package com.interview;

import com.interview.model.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableJpaAuditing
public class CustomerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreate() {
        CustomerDTO newResource = new CustomerDTO("news@cnbsc.com", "Mary", "Cooper", "34 SE Highway, Holliston, MA", (short) 1990);
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/customer", newResource, CustomerDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
    }

    @Test
    public void testCreateFailInvalidEmailFormat() {
        CustomerDTO newResource = new CustomerDTO("bademail", "Mary", "Cooper", "34 SE Highway, Holliston, MA", (short) 1800);
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/customer", newResource, CustomerDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateFailDuplicateEmail() {
        CustomerDTO customer1 = new CustomerDTO("mekanic@auto.com", "Bob", "Cooper", "34 SE Highway, Holliston, MA", (short) 2002);
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/customer", customer1, CustomerDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        response = restTemplate.postForEntity("http://localhost:" + port + "/customer", customer1, CustomerDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteNotFound() {

        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/customer/12", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateThenDelete() {

        CustomerDTO newResource = new CustomerDTO("news@cnbsc.com", "Mary", "Cooper", "34 SE Highway, Holliston, MA", (short) 1990);
        restTemplate.postForEntity("http://localhost:" + port + "/customer", newResource, CustomerDTO.class);

        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/customer/1", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


}
