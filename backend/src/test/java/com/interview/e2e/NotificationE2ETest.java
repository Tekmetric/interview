package com.interview.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AlbumDto;
import com.interview.dto.ArtistDto;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.dto.NotificationMessage;
import com.interview.dto.SongDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import com.interview.repository.AlbumRepository;
import com.interview.repository.ArtistRepository;
import com.interview.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * E2E tests for WebSocket and JMS notifications.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
public class NotificationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    private String wsUrl;

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        songRepository.deleteAll();
        artistRepository.deleteAll();
        wsUrl = "ws://localhost:" + port + "/ws";

        // Clear all JMS queues to prevent test pollution
        clearQueue("artist.queue");
        clearQueue("song.queue");
        clearQueue("album.queue");
    }

    /**
     * Drains all messages from a JMS queue to ensure test isolation.
     */
    private void clearQueue(String queueName) {
        jmsTemplate.setReceiveTimeout(100); // Short timeout for clearing
        Message message;
        do {
            message = jmsTemplate.receive(queueName);
        } while (message != null);
    }

    @Test
    public void testArtistCreateNotificationViaWebSocket() throws Exception {
        BlockingQueue<NotificationMessage> messages = new LinkedBlockingQueue<>();

        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/artists", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((NotificationMessage) payload);
            }
        });

        // Wait a bit for subscription to be established
        Thread.sleep(2000);

        // Create artist via REST API
        ArtistDto artistDto = new ArtistDto();
        artistDto.setName("Queen");

        webTestClient.post()
                .uri("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(artistDto)
                .exchange()
                .expectStatus().isCreated();

        // Wait for WebSocket message
        NotificationMessage message = messages.poll(5, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getAction()).isEqualTo(NotificationAction.CREATE);
        assertThat(message.getEntityType()).isEqualTo(EntityType.ARTIST);
        assertThat(message.getEntityId()).isNotNull();

        session.disconnect();
        stompClient.stop();
    }

    @Test
    public void testArtistUpdateNotificationViaWebSocket() throws Exception {
        // Create artist first
        Artist artist = new Artist("The Beatles");
        artist = artistRepository.save(artist);
        final Long artistId = artist.getId();

        BlockingQueue<NotificationMessage> messages = new LinkedBlockingQueue<>();

        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/artists", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((NotificationMessage) payload);
            }
        });

        Thread.sleep(500);

        // Update artist
        ArtistDto updateDto = new ArtistDto();
        updateDto.setName("The Beatles Updated");

        webTestClient.put()
                .uri("/api/artists/{id}", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk();

        // Verify notification
        NotificationMessage message = messages.poll(5, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getAction()).isEqualTo(NotificationAction.UPDATE);
        assertThat(message.getEntityType()).isEqualTo(EntityType.ARTIST);
        assertThat(message.getEntityId()).isEqualTo(artistId);

        session.disconnect();
        stompClient.stop();
    }

    @Test
    public void testArtistDeleteNotificationViaWebSocket() throws Exception {
        // Create artist first
        Artist artist = new Artist("Queen");
        artist = artistRepository.save(artist);
        final Long artistId = artist.getId();

        BlockingQueue<NotificationMessage> messages = new LinkedBlockingQueue<>();

        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/artists", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((NotificationMessage) payload);
            }
        });

        Thread.sleep(500);

        // Delete artist
        webTestClient.delete()
                .uri("/api/artists/{id}", artistId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify notification
        NotificationMessage message = messages.poll(5, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getAction()).isEqualTo(NotificationAction.DELETE);
        assertThat(message.getEntityType()).isEqualTo(EntityType.ARTIST);
        assertThat(message.getEntityId()).isEqualTo(artistId);

        session.disconnect();
        stompClient.stop();
    }

    @Test
    public void testSongNotificationsViaWebSocket() throws Exception {
        // Create artist first
        Artist artist = new Artist("Queen");
        artist = artistRepository.save(artist);

        BlockingQueue<NotificationMessage> messages = new LinkedBlockingQueue<>();

        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/songs", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((NotificationMessage) payload);
            }
        });

        Thread.sleep(500);

        // Create song
        SongDto songDto = new SongDto();
        songDto.setTitle("Bohemian Rhapsody");
        songDto.setLengthInSeconds(355L);
        songDto.setReleaseDate(LocalDate.of(1975, 10, 31));
        songDto.setArtistId(artist.getId());

        webTestClient.post()
                .uri("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(songDto)
                .exchange()
                .expectStatus().isCreated();

        // Verify CREATE notification
        NotificationMessage createMessage = messages.poll(5, TimeUnit.SECONDS);
        assertThat(createMessage).isNotNull();
        assertThat(createMessage.getAction()).isEqualTo(NotificationAction.CREATE);
        assertThat(createMessage.getEntityType()).isEqualTo(EntityType.SONG);

        session.disconnect();
        stompClient.stop();
    }

    @Test
    public void testAlbumNotificationsViaWebSocket() throws Exception {
        // Create artist first
        Artist artist = new Artist("Queen");
        artist = artistRepository.save(artist);

        BlockingQueue<NotificationMessage> messages = new LinkedBlockingQueue<>();

        WebSocketStompClient stompClient = createStompClient();
        StompSession session = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/albums", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.add((NotificationMessage) payload);
            }
        });

        Thread.sleep(500);

        // Create album
        AlbumDto albumDto = new AlbumDto();
        albumDto.setTitle("A Night at the Opera");
        albumDto.setReleaseDate(LocalDate.of(1975, 11, 21));
        albumDto.setArtistId(artist.getId());

        webTestClient.post()
                .uri("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(albumDto)
                .exchange()
                .expectStatus().isCreated();

        // Verify CREATE notification
        NotificationMessage createMessage = messages.poll(5, TimeUnit.SECONDS);
        assertThat(createMessage).isNotNull();
        assertThat(createMessage.getAction()).isEqualTo(NotificationAction.CREATE);
        assertThat(createMessage.getEntityType()).isEqualTo(EntityType.ALBUM);

        session.disconnect();
        stompClient.stop();
    }

    @Test
    public void testJmsMessageOnArtistCreate() throws Exception {
        // Create artist via REST API
        ArtistDto artistDto = new ArtistDto();
        artistDto.setName("The Rolling Stones");

        webTestClient.post()
                .uri("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(artistDto)
                .exchange()
                .expectStatus().isCreated();

        // Wait a bit for async JMS message
        Thread.sleep(1000);

        // Verify JMS message was sent
        jmsTemplate.setReceiveTimeout(5000); // 5 second timeout
        Message message = jmsTemplate.receive("artist.queue");
        assertThat(message).isNotNull();
        assertThat(message).isInstanceOf(TextMessage.class);

        TextMessage textMessage = (TextMessage) message;
        String json = textMessage.getText();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        NotificationMessage notification = objectMapper.readValue(json, NotificationMessage.class);

        assertThat(notification.getAction()).isEqualTo(NotificationAction.CREATE);
        assertThat(notification.getEntityType()).isEqualTo(EntityType.ARTIST);
        assertThat(notification.getEntityId()).isNotNull();
    }

    @Test
    public void testJmsMessageOnSongUpdate() throws Exception {
        // Create artist and song first
        Artist artist = new Artist("Queen");
        artist = artistRepository.save(artist);

        Song song = new Song("Bohemian Rhapsody", 355L, LocalDate.now(), artist);
        song = songRepository.save(song);
        final Long songId = song.getId();

        // Update song
        SongDto updateDto = new SongDto();
        updateDto.setTitle("Bohemian Rhapsody Updated");
        updateDto.setLengthInSeconds(360L);
        updateDto.setReleaseDate(LocalDate.now());
        updateDto.setArtistId(artist.getId());

        webTestClient.put()
                .uri("/api/songs/{id}", songId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk();

        // Wait a bit for async JMS message
        Thread.sleep(1000);

        // Verify JMS message
        jmsTemplate.setReceiveTimeout(5000); // 5 second timeout
        Message message = jmsTemplate.receive("song.queue");
        assertThat(message).isNotNull();

        TextMessage textMessage = (TextMessage) message;
        String json = textMessage.getText();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        NotificationMessage notification = objectMapper.readValue(json, NotificationMessage.class);

        assertThat(notification.getAction()).isEqualTo(NotificationAction.UPDATE);
        assertThat(notification.getEntityType()).isEqualTo(EntityType.SONG);
        assertThat(notification.getEntityId()).isEqualTo(songId);
    }

    @Test
    public void testJmsMessageOnAlbumDelete() throws Exception {
        // Create artist and album first
        Artist artist = new Artist("Queen");
        artist = artistRepository.save(artist);

        Album album = new Album("A Night at the Opera", LocalDate.now(), artist);
        album = albumRepository.save(album);
        final Long albumId = album.getId();

        // Delete album
        webTestClient.delete()
                .uri("/api/albums/{id}", albumId)
                .exchange()
                .expectStatus().isNoContent();

        // Wait a bit for async JMS message
        Thread.sleep(1000);

        // Verify JMS message
        jmsTemplate.setReceiveTimeout(5000); // 5 second timeout
        Message message = jmsTemplate.receive("album.queue");
        assertThat(message).isNotNull();

        TextMessage textMessage = (TextMessage) message;
        String json = textMessage.getText();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        NotificationMessage notification = objectMapper.readValue(json, NotificationMessage.class);

        assertThat(notification.getAction()).isEqualTo(NotificationAction.DELETE);
        assertThat(notification.getEntityType()).isEqualTo(EntityType.ALBUM);
        assertThat(notification.getEntityId()).isEqualTo(albumId);
    }

    @Test
    public void testMultipleSubscribersReceiveNotifications() throws Exception {
        BlockingQueue<NotificationMessage> messages1 = new LinkedBlockingQueue<>();
        BlockingQueue<NotificationMessage> messages2 = new LinkedBlockingQueue<>();

        // Create two WebSocket clients
        WebSocketStompClient stompClient1 = createStompClient();
        WebSocketStompClient stompClient2 = createStompClient();

        StompSession session1 = stompClient1.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);
        StompSession session2 = stompClient2.connect(wsUrl, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);

        // Both subscribe to artist topic
        session1.subscribe("/topic/artists", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages1.add((NotificationMessage) payload);
            }
        });

        session2.subscribe("/topic/artists", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages2.add((NotificationMessage) payload);
            }
        });

        Thread.sleep(500);

        // Create artist
        ArtistDto artistDto = new ArtistDto();
        artistDto.setName("Led Zeppelin");

        webTestClient.post()
                .uri("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(artistDto)
                .exchange()
                .expectStatus().isCreated();

        // Both subscribers should receive the message
        NotificationMessage message1 = messages1.poll(5, TimeUnit.SECONDS);
        NotificationMessage message2 = messages2.poll(5, TimeUnit.SECONDS);

        assertThat(message1).isNotNull();
        assertThat(message2).isNotNull();
        assertThat(message1.getAction()).isEqualTo(NotificationAction.CREATE);
        assertThat(message2.getAction()).isEqualTo(NotificationAction.CREATE);

        session1.disconnect();
        session2.disconnect();
        stompClient1.stop();
        stompClient2.stop();
    }

    private WebSocketStompClient createStompClient() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        List<org.springframework.web.socket.sockjs.client.Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(webSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().findAndRegisterModules(); // Register JavaTimeModule for LocalDateTime
        stompClient.setMessageConverter(converter);
        return stompClient;
    }
}
