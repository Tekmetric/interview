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
        var repairJob = new RepairJob();
        var userId = UUID.randomUUID().toString();
        repairJob.setJobName("New Test Job");
        repairJob.setUserId(userId);
        repairJob.setRepairDescription("Oil change");
        repairJob.setLicensePlate("ABC1234");
        repairJob.setMake("Toyota");
        repairJob.setModel("Camry");
        repairJob.setStatus(COMPLETED);

        var userId2 = UUID.randomUUID().toString();
        var repairJob2 = new RepairJob();
        repairJob2.setJobName("New Test Job");
        repairJob2.setUserId(userId2);
        repairJob2.setRepairDescription("Oil change");
        repairJob2.setLicensePlate("ABC1234");
        repairJob2.setMake("Toyota");
        repairJob2.setModel("Camry");
        repairJob2.setStatus(COMPLETED);

        repairJobRepository.saveAll(of(repairJob, repairJob2));
        var jobs = repairJobRepository.findRepairJob(userId, null, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindByStatus() {
        var userId = UUID.randomUUID().toString();
        var job = new RepairJob();
        job.setJobName("New Test Job");
        job.setUserId(userId);
        job.setRepairDescription("Oil change");
        job.setLicensePlate("ABC1234");
        job.setMake("Toyota");
        job.setModel("Camry");
        job.setStatus(COMPLETED);

        var job2 = new RepairJob();
        job2.setJobName("New Test Job");
        job2.setUserId(userId);
        job2.setRepairDescription("Oil change");
        job2.setLicensePlate("ABC1234");
        job2.setMake("Toyota");
        job2.setModel("Camry");
        job2.setStatus(CANCELLED);

        repairJobRepository.saveAll(of(job, job2));
        var jobs = repairJobRepository.findRepairJob(null, COMPLETED, null, unpaged());
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }
}
