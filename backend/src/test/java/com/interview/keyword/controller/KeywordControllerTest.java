package com.interview.keyword.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.interview.keyword.dto.KeywordDTO;
import com.interview.keyword.model.Keyword;
import com.interview.keyword.service.KeywordService;

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
import org.springframework.http.*;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KeywordControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // TODO: REMOVE SUPPRESS WARNINGS
    @SuppressWarnings("removal")
    @MockBean
    private KeywordService keywordService;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/keyword";
    }

    @Test
    void testGetKeywords() {
        Keyword keyword = new Keyword();
        keyword.setName("Test Keyword");
        Page<Keyword> page = new PageImpl<>(List.of(keyword));
        PageRequest pageable = PageRequest.of(0, 10);

        when(keywordService.getKeywords(pageable)).thenReturn(page);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void testGetKeywordById() {
        Keyword keyword = new Keyword();
        keyword.setName("Test Keyword");
        keyword.setId(1L);

        when(keywordService.getKeywordById(1L)).thenReturn(keyword);

        ResponseEntity<KeywordDTO> response = restTemplate.getForEntity(baseUrl + "/1", KeywordDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testSaveKeyword() {
        KeywordDTO keywordDTO = new KeywordDTO();
        keywordDTO.setName("New Keyword");

        Keyword keyword = new Keyword();
        keyword.setName("New Keyword");

        when(keywordService.saveKeyword(any(Keyword.class))).thenReturn(keyword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<KeywordDTO> request = new HttpEntity<>(keywordDTO, headers);

        ResponseEntity<KeywordDTO> response = restTemplate.postForEntity(baseUrl, request, KeywordDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteKeyword() {
        doNothing().when(keywordService).deleteKeywordById(anyLong());

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(keywordService, times(1)).deleteKeywordById(anyLong());
    }

    @Test
    void testUpdateKeyword() {
        KeywordDTO keywordDTO = new KeywordDTO();
        keywordDTO.setName("Updated Keyword");

        Keyword updatedKeyword = new Keyword();
        updatedKeyword.setName("Updated Keyword");

        when(keywordService.updateKeyword(1L, "Updated Keyword")).thenReturn(updatedKeyword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<KeywordDTO> request = new HttpEntity<>(keywordDTO, headers);

        ResponseEntity<KeywordDTO> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.PUT, request,
                KeywordDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(keywordService, times(1)).updateKeyword(anyLong(), anyString());
    }
}
