package com.interview.service;

import com.interview.Application;
import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.interview.model.RepairStatus.*;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.data.domain.Pageable.unpaged;

@SpringBootTest(classes = Application.class, webEnvironment = NONE)
public class RepairJobServiceTest {
    @Autowired
    private RepairJobService service;

    @Autowired
    private EntityManager em;

    @Test
    void testGetAllJobs() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Test Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        service.createJob(job);

        var jobs = service.getAllJobs();
        assertThat(jobs.size()).isGreaterThan(1);
    }

    @Test
    void getJobById() {
        // create job
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Test Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        var savedJob = service.createJob(job);

        // now get the job and check the id
        var retrievedJobOptional = service.getJobById(savedJob.getId());

        assertThat(retrievedJobOptional)
                .as("Expected job to be present")
                .isPresent();

        var retrievedJob = retrievedJobOptional.get();

        assertThat(retrievedJob.getId()).isEqualTo(savedJob.getId());
    }

    @Test
    void testCreateJob() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Test Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        var savedJob = service.createJob(job);

        assertThat(savedJob.getId()).isNotNull();
        assertThat(savedJob.getJobName()).isEqualTo(savedJob.getJobName());
        assertThat(savedJob.getUserId()).isEqualTo(savedJob.getUserId());
        assertThat(savedJob.getRepairDescription()).isEqualTo(savedJob.getRepairDescription());
        assertThat(savedJob.getLicensePlate()).isEqualTo(savedJob.getLicensePlate());
        assertThat(savedJob.getMake()).isEqualTo(savedJob.getMake());
        assertThat(savedJob.getModel()).isEqualTo(savedJob.getModel());
        assertThat(savedJob.getStatus()).isEqualTo(CREATED);
    }

    @Test
    @Transactional
    void testUpdateJob() {

        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("Update Test Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);

        var savedJob = service.createJob(job);

        var createdTime = savedJob.getCreated();
        var originalLastModified = savedJob.getLastModified();

        // update request
        var request = new RepairJob();
        request.setJobName("New Job Name");
        request.setUserId("123");
        request.setRepairDescription("none");
        request.setLicensePlate("none");
        request.setMake("none");
        request.setModel("none");
        request.setStatus(IN_PROGRESS);

        service.updateJob(savedJob.getId(), request);

        em.flush();
        em.clear();

        // now get the job again and check for updates
        var reloaded = service.getJobById(savedJob.getId()).orElseThrow();

        assertThat(reloaded.getJobName()).isEqualTo(request.getJobName());
        assertThat(reloaded.getUserId()).isEqualTo(request.getUserId());
        assertThat(reloaded.getRepairDescription()).isEqualTo(request.getRepairDescription());
        assertThat(reloaded.getLicensePlate()).isEqualTo(request.getLicensePlate());
        assertThat(reloaded.getMake()).isEqualTo(request.getModel());
        assertThat(reloaded.getModel()).isEqualTo(request.getModel());

        assertThat(reloaded.getStatus()).isEqualTo(request.getStatus());

        //ensure created is the same
        assertThat(reloaded.getCreated()).isEqualTo(createdTime);
        //ensure lastModified was updated
        assertThat(reloaded.getLastModified()).isAfter(originalLastModified);
    }

    @Test
    void testDeleteJob() {
        var job = createRepairJob();
        var saved = service.createJob(job);
        var id = saved.getId();
        assertThat(id).isNotNull();

        service.deleteJob(id);
        assertThat(service.getJobById(id)).isEqualTo(empty());
    }

    @Test
    void testSearch() {
        var licensePlate = RandomStringUtils.randomAlphabetic(7);
        var licensePlate2 = RandomStringUtils.randomAlphabetic(7);
        var userId = UUID.randomUUID().toString();

        // create jobs
        var job1 = createRepairJob("Need a Engine Diagnostic", userId, "Engine Diagnostic", licensePlate, "Toyota", "Corolla", CREATED);
        var job2 = createRepairJob("Oil Changed Needed", userId, "Oil Change", licensePlate, "Toyota", "Corolla", CANCELLED);
        var job3 = createRepairJob("Oil Changed Needed", userId, "Oil Change", licensePlate2, "Toyota", "Corolla", CANCELLED);
        service.createJob(job1);
        service.createJob(job2);
        service.createJob(job3);

        // search by status and licensePlate
        var jobsByStatus = service.search(null, CREATED, licensePlate, unpaged());
        assertThat(jobsByStatus.get().toList().size()).isEqualTo(1);

        var jobsBy = service.search(null, CANCELLED, licensePlate, unpaged());
        assertThat(jobsBy.get().toList().size()).isEqualTo(1);

        // search by license plate only
        var allLicensePlateJobs = service.search(null, null, licensePlate, unpaged());
        assertThat(allLicensePlateJobs.get().toList().size()).isEqualTo(2);

        // search for all jobs by userid
        var allJobs = service.search(userId, null, null, unpaged());
        assertThat(allJobs.get().toList().size()).isEqualTo(3);
    }

    @Test
    void testSearch_getJobsByUserId() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Test Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        service.createJob(job);
        var jobs = service.search(userId, null, null, unpaged());
        assertThat(jobs.get().toList().size()).isEqualTo(1);
    }

    private RepairJob createRepairJob() {
        var userId = UUID.randomUUID().toString();
        var repairDescription = randomAlphabetic(10);
        var licensePlate = randomAlphabetic(7);
        var make = randomAlphabetic(10);
        var model = randomAlphabetic(10);
        return createRepairJob("Test Repair Job", userId, repairDescription, licensePlate, make, model, CREATED);
    }

    private RepairJob createRepairJob(String jobName, String userId, String repairDescription, String licensePlate, String make, String model, RepairStatus status) {
        var job = new RepairJob();
        job.setJobName(jobName);
        job.setUserId(userId);
        job.setRepairDescription(repairDescription);
        job.setLicensePlate(licensePlate);
        job.setMake(make);
        job.setModel(model);
        job.setStatus(status);
        return job;
    }
}