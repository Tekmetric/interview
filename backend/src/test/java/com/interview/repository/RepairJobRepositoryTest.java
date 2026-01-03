package com.interview.repository;

import com.interview.model.RepairJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static com.interview.model.RepairStatus.CANCELLED;
import static com.interview.model.RepairStatus.COMPLETED;
import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.data.domain.Pageable.unpaged;

@DataJpaTest
public class RepairJobRepositoryTest {
    @Autowired private RepairJobRepository repairJobRepository;

    @Test
    public void testFindByUserId() {
        var userId = UUID.randomUUID().toString();
        var repairJob = new RepairJob();
        repairJob.setUserId(userId);

        var userId2 = UUID.randomUUID().toString();
        var repairJob2 = new RepairJob();
        repairJob2.setUserId(userId2);

        repairJobRepository.saveAll(of(repairJob, repairJob2));
        var jobs = repairJobRepository.findRepairJob(userId, null, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindByStatus() {
        var repairJob = new RepairJob();
        repairJob.setStatus(COMPLETED);
        repairJob.setJobName("New Test Job");

        var repairJob2 = new RepairJob();
        repairJob2.setStatus(CANCELLED);
        repairJob2.setJobName("New Test Job");

        repairJobRepository.saveAll(of(repairJob, repairJob2));
        var jobs = repairJobRepository.findRepairJob(null, COMPLETED, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }
}
