package com.interview.mapper;

import com.interview.fixtures.FruitFixture;
import com.interview.mappers.FruitMapper;
import com.interview.model.Fruit;
import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitPatchRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FruitMapperTest {

    private final FruitMapper mapper = Mappers.getMapper(FruitMapper.class);

    @Test
    void testToEntity_fullRequest() {
        // given
        var request = FruitCreateRequest.builder()
                .name(FruitFixture.FRUIT_NAME_APPLE)
                .color(FruitFixture.COLOR_RED)
                .batchNumber(FruitFixture.BATCH_NUMBER)
                .originCountry(FruitFixture.COUNTRY)
                .category(FruitFixture.CATEGORY)
                .supplier(FruitFixture.SUPPLIER_A)
                .organic(true)
                .quantity(FruitFixture.QUANTITY)
                .registrationDate(Instant.parse("2023-01-01T00:00:00.000Z"))
                .build();

        // when
        var fruit = mapper.toEntity(request);

        // then
        assertNull(fruit.getId());
        assertEquals(request.getName(), fruit.getName());
        assertEquals(request.getColor(), fruit.getColor());
        assertEquals(request.getBatchNumber(), fruit.getBatchNumber());
        assertEquals(request.getOriginCountry(), fruit.getOriginCountry());
        assertEquals(request.getCategory(), fruit.getCategory());
        assertEquals(request.getSupplier(), fruit.getSupplier());
        assertEquals(request.getOrganic(), fruit.getOrganic());
        assertEquals(request.getQuantity(), fruit.getQuantity());
        assertEquals(request.getRegistrationDate(), fruit.getRegistrationDate());
        assertNotNull(fruit.getLastUpdateDate());
    }

    @Test
    void testToEntity_nullRegistrationDate() {
        // given
        var request = FruitCreateRequest.builder()
                .name(FruitFixture.FRUIT_NAME_APPLE)
                .color(FruitFixture.COLOR_RED)
                .batchNumber(FruitFixture.BATCH_NUMBER)
                .originCountry(FruitFixture.COUNTRY)
                .category(FruitFixture.CATEGORY)
                .supplier(FruitFixture.SUPPLIER_A)
                .organic(true)
                .quantity(FruitFixture.QUANTITY)
                .registrationDate(null)
                .build();

        // when
        var fruit = mapper.toEntity(request);

        // then
        assertNotNull(fruit.getRegistrationDate());
        assertNotNull(fruit.getLastUpdateDate());
    }

    @Test
    void testToResponse() {
        // given
        var fruit = FruitFixture.apple();
        fruit.setId(1L);
        fruit.setRegistrationDate(Instant.parse("2023-01-01T00:00:00.000Z"));
        fruit.setLastUpdateDate(Instant.parse("2023-01-02T00:00:00.000Z"));

        // when
        var response = mapper.toResponse(fruit);

        // then
        assertEquals(fruit.getId(), response.getId());
        assertEquals(fruit.getName(), response.getName());
        assertEquals(fruit.getColor(), response.getColor());
        assertEquals(fruit.getBatchNumber(), response.getBatchNumber());
        assertEquals(fruit.getOriginCountry(), response.getOriginCountry());
        assertEquals(fruit.getCategory(), response.getCategory());
        assertEquals(fruit.getSupplier(), response.getSupplier());
        assertEquals(fruit.getOrganic(), response.getOrganic());
        assertEquals(fruit.getQuantity(), response.getQuantity());
        assertEquals(fruit.getRegistrationDate(), response.getRegistrationDate());
        assertEquals(fruit.getLastUpdateDate(), response.getLastUpdateDate());
    }

    @Test
    void testToResponse_null() {
        // when & then
        assertNull(mapper.toResponse((Fruit) null));
    }

    @Test
    void testToResponse_list() {
        // given
        var fruit1 = FruitFixture.apple();
        fruit1.setId(1L);
        var fruit2 = FruitFixture.updatedApple();
        fruit2.setId(2L);
        var fruits = List.of(fruit1, fruit2);

        // when
        var responses = mapper.toResponse(fruits);

        // then
        assertEquals(2, responses.size());
        assertEquals(fruit1.getName(), responses.get(0).getName());
        assertEquals(fruit1.getColor(), responses.get(0).getColor());
        assertEquals(fruit2.getName(), responses.get(1).getName());
        assertEquals(fruit2.getColor(), responses.get(1).getColor());
    }

    @Test
    void testToResponse_emptyList() {
        // when
        var responses = mapper.toResponse(Collections.emptyList());

        // then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testUpdateEntity_allFields() {
        // given
        var request = FruitCreateRequest.builder()
                .name("Banana")
                .color("Yellow")
                .batchNumber("BATCH-002")
                .originCountry("Ecuador")
                .category("Tropical")
                .supplier("SupplierB")
                .organic(false)
                .quantity(20)
                .registrationDate(Instant.parse("2023-02-01T00:00:00.000Z"))
                .build();
        var fruit = FruitFixture.apple();

        // when
        mapper.updateEntity(request, fruit);

        // then
        assertEquals(request.getName(), fruit.getName());
        assertEquals(request.getColor(), fruit.getColor());
        assertEquals(request.getBatchNumber(), fruit.getBatchNumber());
        assertEquals(request.getOriginCountry(), fruit.getOriginCountry());
        assertEquals(request.getCategory(), fruit.getCategory());
        assertEquals(request.getSupplier(), fruit.getSupplier());
        assertEquals(request.getOrganic(), fruit.getOrganic());
        assertEquals(request.getQuantity(), fruit.getQuantity());
        assertEquals(request.getRegistrationDate(), fruit.getRegistrationDate());
        assertNotNull(fruit.getLastUpdateDate());
    }

    @Test
    void testUpdateEntity_nullRegistrationDate() {
        // given
        var request = FruitCreateRequest.builder()
                .name("Banana")
                .color("Yellow")
                .batchNumber("BATCH-002")
                .originCountry("Ecuador")
                .category("Tropical")
                .supplier("SupplierB")
                .organic(false)
                .quantity(20)
                .registrationDate(null)
                .build();
        var fruit = FruitFixture.apple();

        // when
        mapper.updateEntity(request, fruit);

        // then
        assertNotNull(fruit.getRegistrationDate());
        assertNotNull(fruit.getLastUpdateDate());
    }

    @Test
    void testPatchEntity_partialUpdate() {
        // given
        var fruit = FruitFixture.apple();
        var patch = FruitFixture.applePatchRequestGreen();

        // when
        mapper.patchEntity(patch, fruit);

        // then
        assertEquals(FruitFixture.COLOR_GREEN, fruit.getColor());
        assertEquals(FruitFixture.FRUIT_NAME_APPLE, fruit.getName());
        assertEquals(FruitFixture.SUPPLIER_B, fruit.getSupplier());
        assertNotNull(fruit.getLastUpdateDate());
    }

    @Test
    void testPatchEntity_nullFields() {
        // given
        var fruit = FruitFixture.apple();
        var patch = FruitPatchRequest.builder().build();

        // when
        mapper.patchEntity(patch, fruit);

        // then
        assertEquals(FruitFixture.FRUIT_NAME_APPLE, fruit.getName());
        assertEquals(FruitFixture.COLOR_RED, fruit.getColor());
        assertEquals(FruitFixture.BATCH_NUMBER, fruit.getBatchNumber());
        assertEquals(FruitFixture.COUNTRY, fruit.getOriginCountry());
        assertEquals(FruitFixture.CATEGORY, fruit.getCategory());
        assertEquals(FruitFixture.SUPPLIER_A, fruit.getSupplier());
        assertEquals(true, fruit.getOrganic());
        assertEquals(FruitFixture.QUANTITY, fruit.getQuantity());
        assertNotNull(fruit.getLastUpdateDate());
    }
}
