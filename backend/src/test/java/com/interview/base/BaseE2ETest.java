package com.interview.base;

import com.interview.repository.AlbumRepository;
import com.interview.repository.ArtistRepository;
import com.interview.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("e2e")
public abstract class BaseE2ETest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected SongRepository songRepository;

    @Autowired
    protected AlbumRepository albumRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        albumRepository.deleteAll();
        songRepository.deleteAll();
        artistRepository.deleteAll();
    }
}
