package com.interview.actor.controller;

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

import com.interview.actor.dto.ActorDTO;
import com.interview.actor.model.Actor;
import com.interview.actor.service.ActorService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // TODO: REMOVE SUPPRESS WARNINGS
    @SuppressWarnings("removal")
    @MockBean
    private ActorService actorService;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/actor";
    }

    @Test
    void testGetActors() {
        Actor actor = new Actor();
        actor.setFirstName("Test");
        actor.setLastName("Actor");

        Page<Actor> page = new PageImpl<>(List.of(actor));
        PageRequest pageable = PageRequest.of(0, 10);

        when(actorService.getActors(pageable)).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void testGetActorById() {
        Actor actor = new Actor();
        actor.setFirstName("Test");
        actor.setLastName("Actor");

        when(actorService.getActorById(1L)).thenReturn(actor);

        ResponseEntity<ActorDTO> response = restTemplate.getForEntity(baseUrl + "/1", ActorDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ActorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("Test", responseBody.getFirstName());
        assertEquals("Actor", responseBody.getLastName());
    }

    @Test
    void testSaveActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setFirstName("New");
        actorDTO.setLastName("Actor");

        Actor actor = new Actor();
        actor.setFirstName("New");
        actor.setLastName("Actor");

        when(actorService.createActor(any(Actor.class))).thenReturn(actor);

        ResponseEntity<ActorDTO> response = restTemplate.postForEntity(baseUrl, actorDTO, ActorDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        ActorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("New", responseBody.getFirstName());
        assertEquals("Actor", responseBody.getLastName());
    }

    @Test
    void testDeleteActor() {
        doNothing().when(actorService).deleteActorById(anyLong());

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(actorService, times(1)).deleteActorById(anyLong());
    }

    @Test
    void testUpdateActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setFirstName("Updated");
        actorDTO.setLastName("Actor");

        Actor updatedActor = new Actor();
        updatedActor.setFirstName("Updated");
        updatedActor.setLastName("Actor");

        when(actorService.updateActor(anyLong(), any(Actor.class))).thenReturn(updatedActor);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ActorDTO> request = new HttpEntity<>(actorDTO, headers);

        ResponseEntity<ActorDTO> response = restTemplate.exchange(
                baseUrl + "/1",
                HttpMethod.PUT,
                request,
                ActorDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ActorDTO responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals("Updated", responseBody.getFirstName());
        assertEquals("Actor", responseBody.getLastName());
        verify(actorService, times(1)).updateActor(anyLong(), any(Actor.class));
    }
}
