package com.interview.service;

import com.interview.exception.BadRequestException;
import com.interview.exception.NotFoundException;
import com.interview.fixtures.FruitFixture;
import com.interview.mappers.FruitMapperImpl;
import com.interview.model.Fruit;
import com.interview.repository.FruitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.interview.fixtures.FruitFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FruitServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    private FruitService fruitService;

    @BeforeEach
    void setUp() {
        fruitService = new FruitService(fruitRepository, new FruitMapperImpl());
    }

    @Test
    void getAllFruits() {
        // given
        var fruit = FruitFixture.apple();
        when(fruitRepository.findAll()).thenReturn(List.of(fruit));

        // when
        var result = fruitService.getAllFruits();

        // then
        var expectedFruit = FruitFixture.appleResponse();
        assertEquals(1, result.size());
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedFruit);
    }

    @Test
    void getAllFruits_emptyList() {
        // given
        when(fruitRepository.findAll()).thenReturn(List.of());

        // when
        var result = fruitService.getAllFruits();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getFruitsBySupplier() {
        // given
        var fruit = FruitFixture.apple();
        when(fruitRepository.findBySupplier("SupplierA")).thenReturn(List.of(fruit));

        // when
        var result = fruitService.getFruitsBySupplier("SupplierA");

        // then
        var expectedFruit = FruitFixture.appleResponse();
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedFruit);
    }

    @Test
    void getFruitsByBatchNumberAndSupplier() {
        // given
        var fruit = FruitFixture.apple();
        when(fruitRepository.findByBatchNumberAndSupplier("BATCH-001", "SupplierA")).thenReturn(List.of(fruit));

        // when
        var result = fruitService.getFruitsByBatchNumberAndSupplier("BATCH-001", "SupplierA");

        // then
        var expectedFruit = FruitFixture.appleResponse();
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedFruit);
    }

    @Test
    void getFruitById() {
        // given
        var fruit = FruitFixture.apple();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));

        // when
        var result = fruitService.getFruitById(FRUIT_ID);

        // then
        var expectedFruit = FruitFixture.appleResponse();
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedFruit);
    }

    @Test
    void getFruitById_notFound() {
        // given
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> fruitService.getFruitById(FRUIT_ID))
                .withMessageContaining("Fruit not found with id: " + FRUIT_ID);
    }

    @Test
    void create() {
        // given
        var request = FruitFixture.appleCreateRequest();
        var fruit = FruitFixture.apple();
        when(fruitRepository.save(any(Fruit.class))).thenReturn(fruit);

        // when
        var result = fruitService.create(request);

        // then
        var expectedFruit = FruitFixture.appleResponse();
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedFruit);
    }

    @Test
    void create_duplicateFruitThrowsBadRequest() {
        // given
        var request = FruitFixture.appleCreateRequest();
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(request.getName(), request.getSupplier(), request.getBatchNumber())).thenReturn(true);
        // when & then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> fruitService.create(request))
                .withMessageContaining(String.format(
                        "Fruit already exists with name: %s, supplier: %s, batch number: %s",
                        request.getName(), request.getSupplier(), request.getBatchNumber()
                ));
    }

    @Test
    void update() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.appleUpdateRequest();
        var updatedFruit = FruitFixture.updatedApple();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(updatedFruit);

        // when
        var result = fruitService.update(FRUIT_ID, request);

        // then
        var expectedFruit = FruitFixture.updatedAppleResponse();
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedFruit);
    }

    @Test
    void update_notFound() {
        // given
        var request = FruitFixture.appleCreateRequest();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> fruitService.update(FRUIT_ID, request))
                .withMessageContaining("Fruit not found with id: " + FRUIT_ID);
    }

    @Test
    void update_duplicateFruitThrowsBadRequest() {
        // given
        var request = FruitFixture.appleCreateRequest();
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(request.getName(), request.getSupplier(), request.getBatchNumber())).thenReturn(true);

        // when & then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> fruitService.update(FRUIT_ID, request))
                .withMessageContaining(String.format(
                        "Fruit already exists with name: %s, supplier: %s, batch number: %s",
                        request.getName(), request.getSupplier(), request.getBatchNumber()
                ));
    }

    @Test
    void patch() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen();
        var patchedFruit = FruitFixture.updatedApple();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(patchedFruit);

        // when
        var result = fruitService.patch(FRUIT_ID, request);

        // then
        var expectedFruit = FruitFixture.updatedAppleResponse();
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedFruit);
    }

    @Test
    void patch_notFound() {
        // given
        var request = FruitFixture.applePatchRequestGreen();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> fruitService.patch(FRUIT_ID, request))
            .withMessageContaining("Fruit not found with id: " + FRUIT_ID);
    }

    @Test
    void patch_duplicateFruitThrowsBadRequest() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(FRUIT_NAME_APPLE, request.getSupplier(), BATCH_NUMBER))
                .thenReturn(true);

        // when & then
        assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(() -> fruitService.patch(FRUIT_ID, request))
            .withMessageContaining(String.format(
                "Fruit already exists with name: %s, supplier: %s, batch number: %s",
                FRUIT_NAME_APPLE, request.getSupplier(), BATCH_NUMBER
            ));
    }

    @Test
    void patch_changeNameToDuplicate_throwsBadRequest() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen().toBuilder()
                .name(FRUIT_NAME_BANANA)
                .build();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));

        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(
                FRUIT_NAME_BANANA, SUPPLIER_B, BATCH_NUMBER))
            .thenReturn(true);

        // when & then
        assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(() -> fruitService.patch(FRUIT_ID, request))
            .withMessageContaining("Fruit already exists with name: Banana, supplier: SupplierB, batch number: BATCH-001");
    }

    @Test
    void patch_changeNameNoDuplicate_succeeds() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen().toBuilder().name(FRUIT_NAME_BANANA).build();
        var patchedFruit = fruit.toBuilder().name(FRUIT_NAME_BANANA).build();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(
                FRUIT_NAME_BANANA, SUPPLIER_B, fruit.getBatchNumber()))
            .thenReturn(false);
        when(fruitRepository.save(any(Fruit.class))).thenReturn(patchedFruit);

        // when
        var result = fruitService.patch(FRUIT_ID, request);

        // then
        assertThat(result.getName()).isEqualTo(FRUIT_NAME_BANANA);
    }

    @Test
    void patch_changeTwoFieldsToDuplicate_throwsBadRequest() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen().toBuilder()
                .name(FRUIT_NAME_BANANA)
                .build();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(
                eq(FRUIT_NAME_BANANA), eq(SUPPLIER_B), eq(fruit.getBatchNumber())))
            .thenReturn(true);

        // when & then
        assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(() -> fruitService.patch(FRUIT_ID, request))
            .withMessageContaining("Fruit already exists with name: Banana, supplier: SupplierB, batch number: BATCH-001");
    }

    @Test
    void patch_changeTwoFieldsNoDuplicate_succeeds() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen().toBuilder().name(FRUIT_NAME_BANANA).supplier(SUPPLIER_B).build();
        var patchedFruit = fruit.toBuilder().name(FRUIT_NAME_BANANA).supplier(SUPPLIER_B).build();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.existsByNameAndSupplierAndBatchNumber(
                eq(FRUIT_NAME_BANANA), eq(SUPPLIER_B), eq(fruit.getBatchNumber())))
            .thenReturn(false);
        when(fruitRepository.save(any(Fruit.class))).thenReturn(patchedFruit);

        // when
        var result = fruitService.patch(FRUIT_ID, request);

        // then
        assertThat(result.getName()).isEqualTo(FRUIT_NAME_BANANA);
        assertThat(result.getSupplier()).isEqualTo(SUPPLIER_B);
    }

    @Test
    void patch_noUniqueFieldChange_noDuplicateCheck() {
        // given
        var fruit = FruitFixture.apple();
        var request = FruitFixture.applePatchRequestGreen().toBuilder().name(null).supplier(null).batchNumber(null).build();
        var patchedFruit = fruit.toBuilder().color(COLOR_GREEN).build();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(patchedFruit);

        // when
        var result = fruitService.patch(FRUIT_ID, request);

        // then
        assertThat(result.getColor()).isEqualTo(COLOR_GREEN);
    }

    @Test
    void delete() {
        // given
        var fruit = FruitFixture.apple();
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.of(fruit));
        doNothing().when(fruitRepository).delete(fruit);

        // when
        fruitService.delete(FRUIT_ID);

        // then
        verify(fruitRepository, times(1)).delete(fruit);
    }

    @Test
    void delete_notFound() {
        // given
        when(fruitRepository.findById(FRUIT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> fruitService.delete(FRUIT_ID))
            .withMessageContaining("Fruit not found with id: " + FRUIT_ID);
    }
}
