package com.interview.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class TestFixtureLoader {

    private static final String FIXTURE_BASE = "fixtures/";

    public static String load(String relativePath) {
        String fullPath = FIXTURE_BASE + relativePath;
        try (InputStream is = TestFixtureLoader.class.getClassLoader().getResourceAsStream(fullPath)) {
            Objects.requireNonNull(is, "Fixture not found on classpath: " + fullPath);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixture: " + fullPath, e);
        }
    }
}
