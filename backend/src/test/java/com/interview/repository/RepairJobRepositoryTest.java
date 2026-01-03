package com.interview.repository;

import com.interview.model.RepairJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static com.interview.model.RepairStatus.COMPLETED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class RepairJobRepositoryTest {
    @Autowired private RepairJobRepository repairJobRepository;

    @Test
    public void testFindByUserId() {
        var userId = UUID.randomUUID().toString();
        var repairJob = new RepairJob();
        repairJob.setUserId(userId);

        repairJobRepository.save(repairJob);
        assertThat(repairJobRepository.findByUserId(userId).size())
                .isEqualTo(1);
    }

    @Test
    public void testFindByStatus() {
        var repairJob = new RepairJob();
        repairJob.setStatus(COMPLETED);
        repairJob.setJobName("New Test Job");

        repairJobRepository.save(repairJob);
        var jobs = repairJobRepository.findByStatus(COMPLETED);
        assertThat(jobs.size()).isEqualTo(1);
    }
}
