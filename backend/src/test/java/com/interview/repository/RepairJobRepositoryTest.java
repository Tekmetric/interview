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
                .name("Oil Change")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("Oil change")
                .status(COMPLETED)
                .build();

        var userId2 = UUID.randomUUID().toString();
        var repairJob2 = RepairJob.builder()
                .name("Oil Change")
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
                .name("WindowShield Repair")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("repair window shield")
                .status(COMPLETED)
                .build();

        var job2 = RepairJob.builder()
                .name("WindowShield Repair")
                .userId(userId)
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Camry")
                .repairDescription("repair window shield")
                .status(CANCELLED)
                .build();

        repairJobRepository.saveAll(of(job, job2));
        var completedJobs = repairJobRepository.search(null, COMPLETED, null, unpaged());
        assertThat(completedJobs.getTotalElements()).isEqualTo(1);

        var cancelledJobs = repairJobRepository.search(null, CANCELLED, null, unpaged());
        assertThat(cancelledJobs.getTotalElements()).isEqualTo(1);
    }
}
