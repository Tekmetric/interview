package com.interview.fixtures;

import com.interview.model.Fruit;
import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitPatchRequest;
import com.interview.model.FruitResponse;

public class FruitFixture {

    public static final Long FRUIT_ID = 1L;
    public static final String FRUIT_NAME_APPLE = "Apple";
    public static final String FRUIT_NAME_BANANA = "Banana";
    public static final String SUPPLIER_A = "SupplierA";
    public static final String SUPPLIER_B = "SupplierB";
    public static final String BATCH_NUMBER = "BATCH-001";
    public static final String COLOR_RED = "Red";
    public static final String COLOR_GREEN = "Green";
    public static final String COUNTRY = "Romania";
    public static final String CATEGORY = "Pome";
    public static final Integer QUANTITY = 10;

    public static Fruit apple() {
        return Fruit.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_RED)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_A)
                .organic(true)
                .quantity(10)
                .build();
    }

    public static Fruit updatedApple() {
        return Fruit.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_GREEN)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_B)
                .organic(true)
                .quantity(10)
                .build();
    }

    public static FruitResponse appleResponse() {
        return FruitResponse.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_RED)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_A)
                .organic(true)
                .quantity(10)
                .build();
    }

    public static FruitCreateRequest appleCreateRequest() {
        return FruitCreateRequest.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_RED)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_A)
                .organic(true)
                .quantity(10)
                .build();
    }

    public static FruitCreateRequest appleUpdateRequest() {
        return FruitCreateRequest.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_GREEN)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_B)
                .organic(true)
                .quantity(10)
                .build();
    }

    public static FruitPatchRequest applePatchRequestGreen() {
        return FruitPatchRequest.builder()
                .color(COLOR_GREEN)
                .supplier(SUPPLIER_B)
                .build();
    }

    public static FruitResponse updatedAppleResponse() {
        return FruitResponse.builder()
                .name(FRUIT_NAME_APPLE)
                .color(COLOR_GREEN)
                .batchNumber(BATCH_NUMBER)
                .originCountry(COUNTRY)
                .category(CATEGORY)
                .supplier(SUPPLIER_B)
                .organic(true)
                .quantity(10)
                .build();
    }
}
