package com.interview.repository;

import com.interview.model.RepairJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static com.interview.model.RepairStatus.COMPLETED;
import static com.interview.model.RepairStatus.CANCELLED;
import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.data.domain.Pageable.unpaged;

@DataJpaTest
public class RepairJobRepositoryTest {
    @Autowired private RepairJobRepository repairJobRepository;

    @Test
    public void testFindByUserId() {
        var userId = UUID.randomUUID().toString();
        var repairJob = RepairJob.builder()
                .name("New Test Job")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("Oil change")
                .status(COMPLETED)
                .build();

        var userId2 = UUID.randomUUID().toString();
        var repairJob2 = RepairJob.builder()
                .name("New Test Job")
                .userId(userId2)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("Oil change")
                .status(COMPLETED)
                .build();

        repairJobRepository.saveAll(of(repairJob, repairJob2));
        var jobs = repairJobRepository.search(userId, null, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindByStatus() {
        var userId = UUID.randomUUID().toString();
        var job = RepairJob.builder()
                .name("New Test Job")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("repair")
                .status(COMPLETED)
                .build();

        var job2 = RepairJob.builder()
                .name("New Test Job")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("Oil Change")
                .status(CANCELLED)
                .build();

        repairJobRepository.saveAll(of(job, job2));
        var jobs = repairJobRepository.search(null, COMPLETED, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }
}
