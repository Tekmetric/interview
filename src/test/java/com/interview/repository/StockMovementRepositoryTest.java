package com.interview.repository;

import com.interview.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Stock Movement Repository Tests")
class StockMovementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockMovementRepository movementRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Warehouse warehouse1;
    private Warehouse warehouse2;
    private StockMovement inMovement;
    private StockMovement outMovement;
    private StockMovement oldMovement;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product("TEST-001", "Test Product 1", "Electronics", "each", new BigDecimal("99.99"));
        testProduct2 = new Product("TEST-002", "Test Product 2", "Electronics", "each", new BigDecimal("199.99"));
        
        warehouse1 = new Warehouse("Main Warehouse", "Location 1");
        warehouse2 = new Warehouse("Secondary Warehouse", "Location 2");

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);
        entityManager.persistAndFlush(warehouse1);
        entityManager.persistAndFlush(warehouse2);

        // Recent IN movement
        inMovement = new StockMovement(testProduct1, warehouse1, MovementType.IN, MovementReason.PURCHASE, 50);
        inMovement.setUnitCost(BigDecimal.valueOf(99.99));
        inMovement.setReferenceNumber("PO-001");
        inMovement.setNotes("Initial stock purchase");
        inMovement.setCreatedAt(LocalDateTime.now().minusHours(2));

        // Recent OUT movement
        outMovement = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 10);
        outMovement.setUnitCost(BigDecimal.valueOf(99.99));
        outMovement.setReferenceNumber("SO-001");
        outMovement.setNotes("Customer sale");
        outMovement.setCreatedAt(LocalDateTime.now().minusHours(1));

        // Old movement (outside recent range)
        oldMovement = new StockMovement(testProduct2, warehouse2, MovementType.IN, MovementReason.ADJUSTMENT, 25);
        oldMovement.setUnitCost(BigDecimal.valueOf(199.99));
        oldMovement.setReferenceNumber("ADJ-001");
        oldMovement.setNotes("Inventory adjustment");
        oldMovement.setCreatedAt(LocalDateTime.now().minusDays(35));

        entityManager.persistAndFlush(inMovement);
        entityManager.persistAndFlush(outMovement);
        entityManager.persistAndFlush(oldMovement);
    }

    @Test
    @DisplayName("Should find recent movements by product")
    void shouldFindRecentMovementsByProduct() {
        // When
        List<StockMovement> result = movementRepository.findRecentMovementsByProduct(testProduct1.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockMovement::getReferenceNumber)
                .containsExactlyInAnyOrder("PO-001", "SO-001");
        assertThat(result).extracting(StockMovement::getProduct)
                .containsOnly(testProduct1);
    }

    @Test
    @DisplayName("Should find movements by warehouse")
    void shouldFindMovementsByWarehouse() {
        // When
        List<StockMovement> result = movementRepository.findByWarehouse(warehouse1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockMovement::getWarehouse)
                .containsOnly(warehouse1);
        assertThat(result).extracting(StockMovement::getMovementType)
                .containsExactlyInAnyOrder(MovementType.IN, MovementType.OUT);
    }

    @Test
    @DisplayName("Should find movements by product and warehouse")
    void shouldFindMovementsByProductAndWarehouse() {
        // When
        List<StockMovement> result = movementRepository.findByProductAndWarehouse(testProduct1, warehouse1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockMovement::getProduct)
                .containsOnly(testProduct1);
        assertThat(result).extracting(StockMovement::getWarehouse)
                .containsOnly(warehouse1);
    }

    @Test
    @DisplayName("Should find movements by product")
    void shouldFindMovementsByProduct() {
        // When
        List<StockMovement> result = movementRepository.findByProduct(testProduct1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockMovement::getProduct)
                .containsOnly(testProduct1);
        assertThat(result).extracting(StockMovement::getMovementReason)
                .containsExactlyInAnyOrder(MovementReason.PURCHASE, MovementReason.SALE);
    }

    @Test
    @DisplayName("Should find movements in date range")
    void shouldFindMovementsInDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        // When
        List<StockMovement> result = movementRepository.findByCreatedAtBetween(startDate, endDate);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StockMovement::getReferenceNumber)
                .containsExactlyInAnyOrder("PO-001", "SO-001");
        assertThat(result).extracting(StockMovement::getCreatedAt)
                .allMatch(date -> date.isAfter(startDate) && date.isBefore(endDate));
    }

    @Test
    @DisplayName("Should verify movement quantities and types")
    void shouldVerifyMovementQuantitiesAndTypes() {
        // When
        List<StockMovement> movements = movementRepository.findByProduct(testProduct1);

        // Then
        assertThat(movements).hasSize(2);
        
        StockMovement purchase = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.IN)
                .findFirst().orElseThrow();
        assertThat(purchase.getQuantity()).isEqualTo(50);
        assertThat(purchase.getSignedQuantity()).isEqualTo(50);
        
        StockMovement sale = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.OUT)
                .findFirst().orElseThrow();
        assertThat(sale.getQuantity()).isEqualTo(10);
        assertThat(sale.getSignedQuantity()).isEqualTo(-10);
    }

    @Test
    @DisplayName("Should verify movement persistence and retrieval")
    void shouldVerifyMovementPersistenceAndRetrieval() {
        // Create a new movement
        StockMovement newMovement = new StockMovement(testProduct2, warehouse1, 
                MovementType.IN, MovementReason.ADJUSTMENT, 25);
        newMovement.setUnitCost(BigDecimal.valueOf(199.99));
        newMovement.setReferenceNumber("TEST-001");
        newMovement.setNotes("Test movement");

        // When
        StockMovement saved = movementRepository.save(newMovement);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getReferenceNumber()).isEqualTo("TEST-001");
        assertThat(saved.getNotes()).isEqualTo("Test movement");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle empty results for non-existent data")
    void shouldHandleEmptyResultsForNonExistentData() {
        // Create a new warehouse with no movements
        Warehouse emptyWarehouse = new Warehouse("Empty Warehouse", "Empty Location");
        entityManager.persistAndFlush(emptyWarehouse);

        // When
        List<StockMovement> result = movementRepository.findByWarehouse(emptyWarehouse);

        // Then
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("Should order movements by creation date descending")
    void shouldOrderMovementsByCreationDateDescending() {
        // When
        List<StockMovement> result = movementRepository.findRecentMovementsByProduct(testProduct1.getId());

        // Then
        assertThat(result).hasSize(2);
        // Should be ordered by creation date descending (most recent first)
        assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt());
        assertThat(result.get(0)).isEqualTo(outMovement); // More recent
        assertThat(result.get(1)).isEqualTo(inMovement); // Less recent
    }

    @Test
    @DisplayName("Should return empty list for non-existent product movements")
    void shouldReturnEmptyListForNonExistentProductMovements() {
        // When
        List<StockMovement> result = movementRepository.findRecentMovementsByProduct(999L);

        // Then
        assertThat(result).isEmpty();
    }
}