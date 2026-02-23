package com.interview.idempotency;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyStore {

    private static final long TTL_MILLIS = 24 * 60 * 60 * 1000L;

    private final ConcurrentHashMap<String, IdempotencyRecord> store = new ConcurrentHashMap<>();

    public IdempotencyRecord get(String key) {
        IdempotencyRecord record = store.get(key);
        if (record == null) {
            return null;
        }
        if (System.currentTimeMillis() - record.createdAtMillis() > TTL_MILLIS) {
            store.remove(key);
            return null;
        }
        return record;
    }

    public void put(String key, IdempotencyRecord record) {
        store.put(key, record);
    }
}
