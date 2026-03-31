package com.interview.repository;

import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepairOrderRepositoryTest {

    @Autowired
    private RepairOrderRepository repository;

    @Test
    void existsByVehicleVinReturnsTrueWhenPresent() {
        assertThat(repository.existsByVehicleVin("1HGCM82633A004352")).isTrue();
    }

    @Test
    void existsByVehicleVinReturnsTrueAfterSave() {
        RepairOrder order = new RepairOrder();
        order.setCustomerName("Test Customer");
        order.setVehicleVin("JM1BK32F581234567");
        order.setDescription("Test repair");
        order.setStatus(RepairOrderStatus.OPEN);
        order.setTotalCost(new java.math.BigDecimal("100.00"));

        repository.save(order);

        assertThat(repository.existsByVehicleVin("JM1BK32F581234567")).isTrue();
    }

    @Test
    void existsByVehicleVinReturnsFalseWhenMissing() {
        boolean exists = repository.existsByVehicleVin("DOESNOTEXIST12345");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByVehicleVinAndIdNotReturnsFalseForSameRecord() {
        RepairOrder saved = repository.findAll().stream()
                .filter(order -> "1HGCM82633A004352".equals(order.getVehicleVin()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seeded repair order not found"));

        assertThat(repository.existsByVehicleVinAndIdNot("1HGCM82633A004352", saved.getId())).isFalse();
    }

    @Test
    void existsByVehicleVinAndIdNotReturnsTrueForDifferentRecord() {
        RepairOrder saved = repository.findAll().stream()
                .filter(order -> "1HGCM82633A004352".equals(order.getVehicleVin()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Seeded repair order not found"));

        assertThat(repository.existsByVehicleVinAndIdNot("1HGCM82633A004352", saved.getId() + 999)).isTrue();
    }
}
