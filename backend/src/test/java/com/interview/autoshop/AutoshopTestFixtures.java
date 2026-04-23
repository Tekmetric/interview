package com.interview.autoshop;

public final class AutoshopTestFixtures {

    public static final String SEED_FIRST_NAME = "Hopper Motors";
    public static final long SEED_FIRST_ID = 1L;
    public static final long SEED_MISSING_ID = 9999L;

    private AutoshopTestFixtures() {}

    public static String createRequestJson(String name, String address, String phone) {
        return "{\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\"}".formatted(name, address, phone);
    }
}
