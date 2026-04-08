package com.interview.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.repository.IdempotencyRepository;
import com.interview.repository.model.IdempotencyRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private static final Duration TTL = Duration.ofHours(24);

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;

    public Optional<CachedResponse> find(String idempotencyKey) {
        return repository.findById(idempotencyKey)
                .map(record -> {
                    log.debug("Idempotency cache hit for key={}", idempotencyKey);
                    return new CachedResponse(record.getResponseStatus(), record.getResponseBody());
                });
    }

    @Transactional
    public void store(String idempotencyKey, int httpStatus, Object responseBody) {
        try {
            String bodyJson = objectMapper.writeValueAsString(responseBody);
            Instant now = Instant.now();
            repository.save(IdempotencyRecord.builder()
                    .idempotencyKey(idempotencyKey)
                    .responseStatus(httpStatus)
                    .responseBody(bodyJson)
                    .createdAt(now)
                    .expiresAt(now.plus(TTL))
                    .build());
            log.debug("Stored idempotency record for key={}", idempotencyKey);
        } catch (Exception e) {
            log.warn("Failed to store idempotency record for key={}: {}", idempotencyKey, e.getMessage());
        }
    }

    public record CachedResponse(int status, String bodyJson) {
    }
}
