package com.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.repository.IdempotencyRepository;
import com.interview.repository.model.IdempotencyRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @InjectMocks
    private IdempotencyService idempotencyService;

    @Mock
    private IdempotencyRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private final String key = "some-key";

    @Test
    void find_returnsCachedResponse_whenRecordExists() {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(key)
                .responseStatus(201)
                .responseBody("{\"msg\":\"ok\"}")
                .build();
        when(repository.findById(key)).thenReturn(Optional.of(record));

        Optional<IdempotencyService.CachedResponse> result = idempotencyService.find(key);

        assertThat(result).isPresent();
        assertThat(result.get()
                .status()).isEqualTo(201);
        assertThat(result.get()
                .bodyJson()).isEqualTo("{\"msg\":\"ok\"}");
        verify(repository).findById(key);
    }

    @Test
    void find_returnsEmpty_whenRecordNotFound() {
        when(repository.findById(key)).thenReturn(Optional.empty());

        Optional<IdempotencyService.CachedResponse> result = idempotencyService.find(key);

        assertThat(result).isEmpty();
        verify(repository).findById(key);
    }

    @Test
    void store_savesRecordSuccessfully() throws Exception {
        Object response = new Object();
        String json = "{\"msg\":\"ok\"}";
        when(objectMapper.writeValueAsString(response)).thenReturn(json);

        idempotencyService.store(key, 201, response);

        ArgumentCaptor<IdempotencyRecord> captor = ArgumentCaptor.forClass(IdempotencyRecord.class);
        verify(repository).save(captor.capture());
        IdempotencyRecord saved = captor.getValue();

        assertThat(saved.getIdempotencyKey()).isEqualTo(key);
        assertThat(saved.getResponseStatus()).isEqualTo(201);
        assertThat(saved.getResponseBody()).isEqualTo(json);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getExpiresAt()).isAfter(saved.getCreatedAt());
    }

    @Test
    void store_logsWarning_whenObjectMapperThrows() throws Exception {
        Object response = new Object();
        when(objectMapper.writeValueAsString(response)).thenThrow(new RuntimeException("oops"));

        idempotencyService.store(key, 200, response);

        verify(repository, never()).save(any());
    }
}