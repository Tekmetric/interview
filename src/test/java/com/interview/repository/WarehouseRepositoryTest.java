package com.interview.repository;

import com.interview.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Warehouse Repository Tests")
class WarehouseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private Warehouse activeWarehouse1;
    private Warehouse activeWarehouse2;
    private Warehouse inactiveWarehouse;

    @BeforeEach
    void setUp() {
        activeWarehouse1 = new Warehouse("Main Warehouse", "123 Main St, City A");
        activeWarehouse2 = new Warehouse("Secondary Warehouse", "456 Oak Ave, City B");
        inactiveWarehouse = new Warehouse("Old Warehouse", "789 Pine Rd, City C");
        inactiveWarehouse.setActive(false);

        entityManager.persistAndFlush(activeWarehouse1);
        entityManager.persistAndFlush(activeWarehouse2);
        entityManager.persistAndFlush(inactiveWarehouse);
    }

    @Test
    @DisplayName("Should find warehouse by ID")
    void shouldFindWarehouseById() {
        // When
        Optional<Warehouse> result = warehouseRepository.findById(activeWarehouse1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Main Warehouse");
        assertThat(result.get().getLocation()).isEqualTo("123 Main St, City A");
    }

    @Test
    @DisplayName("Should return empty when warehouse ID not found")
    void shouldReturnEmptyWhenWarehouseIdNotFound() {
        // When
        Optional<Warehouse> result = warehouseRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find only active warehouses")
    void shouldFindOnlyActiveWarehouses() {
        // When
        List<Warehouse> result = warehouseRepository.findByActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Warehouse::getName)
                .containsExactlyInAnyOrder("Main Warehouse", "Secondary Warehouse");
        assertThat(result).allMatch(Warehouse::getActive);
    }

    @Test
    @DisplayName("Should verify warehouse persistence and retrieval")
    void shouldVerifyWarehousePersistenceAndRetrieval() {
        // Create a new warehouse
        Warehouse newWarehouse = new Warehouse("Test Warehouse", "Test Location");

        // When
        Warehouse saved = warehouseRepository.save(newWarehouse);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Warehouse");
        assertThat(saved.getLocation()).isEqualTo("Test Location");
        assertThat(saved.getActive()).isTrue(); // Default should be true
    }

    @Test
    @DisplayName("Should find all warehouses including inactive ones")
    void shouldFindAllWarehousesIncludingInactiveOnes() {
        // When
        List<Warehouse> result = warehouseRepository.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Warehouse::getName)
                .containsExactlyInAnyOrder("Main Warehouse", "Secondary Warehouse", "Old Warehouse");
    }

    @Test
    @DisplayName("Should update warehouse status")
    void shouldUpdateWarehouseStatus() {
        // Given
        assertThat(activeWarehouse1.getActive()).isTrue();

        // When
        activeWarehouse1.setActive(false);
        Warehouse updated = warehouseRepository.save(activeWarehouse1);

        // Then
        assertThat(updated.getActive()).isFalse();
        
        // Verify active warehouses count decreased
        List<Warehouse> activeWarehouses = warehouseRepository.findByActiveTrue();
        assertThat(activeWarehouses).hasSize(1);
    }

    @Test
    @DisplayName("Should delete warehouse")
    void shouldDeleteWarehouse() {
        // Given
        Long warehouseId = activeWarehouse2.getId();
        assertThat(warehouseRepository.existsById(warehouseId)).isTrue();

        // When
        warehouseRepository.delete(activeWarehouse2);

        // Then
        assertThat(warehouseRepository.existsById(warehouseId)).isFalse();
        
        // Verify total count decreased
        List<Warehouse> allWarehouses = warehouseRepository.findAll();
        assertThat(allWarehouses).hasSize(2);
    }

    @Test
    @DisplayName("Should verify warehouse entity fields")
    void shouldVerifyWarehouseEntityFields() {
        // When
        Warehouse warehouse = warehouseRepository.findById(activeWarehouse1.getId()).orElseThrow();

        // Then
        assertThat(warehouse.getId()).isNotNull();
        assertThat(warehouse.getName()).isNotBlank();
        assertThat(warehouse.getLocation()).isNotBlank();
        assertThat(warehouse.getActive()).isNotNull();
    }

    @Test
    @DisplayName("Should handle batch operations")
    void shouldHandleBatchOperations() {
        // Create multiple warehouses
        List<Warehouse> newWarehouses = List.of(
                new Warehouse("Batch Warehouse 1", "Batch Location 1"),
                new Warehouse("Batch Warehouse 2", "Batch Location 2")
        );

        // When
        List<Warehouse> saved = warehouseRepository.saveAll(newWarehouses);

        // Then
        assertThat(saved).hasSize(2);
        assertThat(saved).allMatch(w -> w.getId() != null);
        assertThat(saved).extracting(Warehouse::getName)
                .containsExactlyInAnyOrder("Batch Warehouse 1", "Batch Warehouse 2");
    }
}