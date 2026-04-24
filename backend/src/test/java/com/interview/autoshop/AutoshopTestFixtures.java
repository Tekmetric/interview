package com.interview.autoshop;

public final class AutoshopTestFixtures {

    public static final String SEED_FIRST_NAME = "Hopper Motors";
    public static final long SEED_FIRST_ID = 1L;
    public static final long SEED_MISSING_ID = 9999L;

    private AutoshopTestFixtures() {}

    public static String createRequestJson(String name, String address, String phone) {
        return "{\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\"}".formatted(name, address, phone);
    }

    public static String validCreateJson() {
        return createRequestJson("New Shop", "1 New Rd", "555-0000");
    }

    public static String invalidCreateJson_blankName() {
        return createRequestJson("", "1 New Rd", "555-0000");
    }
}
