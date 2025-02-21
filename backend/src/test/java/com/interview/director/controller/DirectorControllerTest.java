package com.interview.director.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.interview.director.dto.DirectorDTO;
import com.interview.director.model.Director;
import com.interview.director.service.DirectorService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DirectorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // TODO: REMOVE SUPPRESS WARNINGS
    @SuppressWarnings("removal")
    @MockBean
    private DirectorService directorService;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/director";
    }

    @Test
    void testGetDirectors() {
        Director director = new Director();
        director.setFirstName("Test");
        director.setLastName("Actor");

        Page<Director> page = new PageImpl<>(List.of(director));
        PageRequest pageable = PageRequest.of(0, 10);

        when(directorService.getDirectors(pageable)).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void testGetDirectorById() {
        Director actor = new Director();
        actor.setFirstName("Test");
        actor.setLastName("Director");

        when(directorService.getDirectorById(anyLong())).thenReturn(actor);

        ResponseEntity<DirectorDTO> response = restTemplate.getForEntity(baseUrl + "/1", DirectorDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        DirectorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("Test", responseBody.getFirstName());
        assertEquals("Director", responseBody.getLastName());
    }

    @Test
    void testSaveDirector() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setFirstName("New");
        directorDTO.setLastName("Director");

        Director director = new Director();
        director.setFirstName("New");
        director.setLastName("Director");

        when(directorService.createDirector(any(Director.class))).thenReturn(director);

        ResponseEntity<DirectorDTO> response = restTemplate.postForEntity(baseUrl, directorDTO, DirectorDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        DirectorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("New", responseBody.getFirstName());
        assertEquals("Director", responseBody.getLastName());
    }

    @Test
    void testDeleteDirector() {
        doNothing().when(directorService).deleteDirectorById(anyLong());

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(directorService, times(1)).deleteDirectorById(anyLong());
    }

    @Test
    void testUpdateDirector() {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setFirstName("Updated");
        directorDTO.setLastName("Director");

        Director updatedDirector = new Director();
        updatedDirector.setFirstName("Updated");
        updatedDirector.setLastName("Director");

        when(directorService.updateDirector(anyLong(), any(Director.class))).thenReturn(updatedDirector);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DirectorDTO> request = new HttpEntity<>(directorDTO, headers);

        ResponseEntity<DirectorDTO> response = restTemplate.exchange(
                baseUrl + "/1",
                HttpMethod.PUT,
                request,
                DirectorDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        DirectorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("Updated", responseBody.getFirstName());
        assertEquals("Director", responseBody.getLastName());
        verify(directorService, times(1)).updateDirector(anyLong(), any(Director.class));
    }

}
