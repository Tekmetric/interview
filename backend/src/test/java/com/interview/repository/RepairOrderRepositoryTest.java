package com.interview.repository;

import com.interview.domain.RepairOrder;
import com.interview.domain.RepairOrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepairOrderRepositoryTest {

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Test
    @Transactional
    void saveAndUpdate_populatesTimestampsAndIncrementsVersion() {
        RepairOrder saved = repairOrderRepository.save(
                new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN)
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Long versionBefore = saved.getVersion();
        Instant updatedAtBefore = saved.getUpdatedAt();

        saved.update("Customer B", "Updated", RepairOrderStatus.COMPLETED);
        RepairOrder updated = repairOrderRepository.saveAndFlush(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getVersion()).isNotNull();
        assertThat(updated.getVersion()).isGreaterThan(versionBefore);
        assertThat(updated.getUpdatedAt()).isAfter(updatedAtBefore);
        assertThat(updated.getCustomerName()).isEqualTo("Customer B");
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getStatus()).isEqualTo(RepairOrderStatus.COMPLETED);
    }

    @Test
    @Transactional
    void delete_removesEntity() {
        RepairOrder saved = repairOrderRepository.save(
                new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN)
        );

        assertThat(repairOrderRepository.existsById(saved.getId())).isTrue();

        repairOrderRepository.deleteById(saved.getId());

        assertThat(repairOrderRepository.existsById(saved.getId())).isFalse();
    }
}
