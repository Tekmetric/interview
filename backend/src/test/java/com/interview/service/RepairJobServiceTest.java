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
        var job = createRepairJob("First Repair Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        service.createJob(job);

        var jobs = service.getAllJobs();
        assertThat(jobs.size()).isGreaterThan(1);
    }

    @Test
    void getJobById() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Repair Job", userId, "Engine Diagnostic",
                "IT1234", "Toyota", "Corolla", CREATED);

        var savedJob = service.createJob(job);
        assertThat(service.getJobById(savedJob.getId()))
                .as("Expected job lookup to return saved job")
                .isPresent()
                .get()
                .extracting(RepairJob::getId)
                .isEqualTo(savedJob.getId());
    }

    @Test
    void testCreateJob() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Repair Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        var savedJob = service.createJob(job);

        assertThat(savedJob.getId()).isNotNull();
        assertThat(savedJob.getName()).isEqualTo(savedJob.getName());
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
        var request = RepairJob.builder()
                .name("New Job Name")
                .userId("123")
                .repairDescription("none")
                .licensePlate("none")
                .make("none")
                .model("none")
                .status(IN_PROGRESS).build();

        service.updateJob(savedJob.getId(), request);

        em.flush();
        em.clear();

        // now get the job again and check for updates
        var reloaded = service.getJobById(savedJob.getId()).orElseThrow();

        assertThat(reloaded.getName()).isEqualTo(request.getName());
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
        assertThat(jobsByStatus.getContent().size()).isEqualTo(1);

        var jobsBy = service.search(null, CANCELLED, licensePlate, unpaged());
        assertThat(jobsBy.getContent().size()).isEqualTo(1);

        // search by license plate only
        var allLicensePlateJobs = service.search(null, null, licensePlate, unpaged());
        assertThat(allLicensePlateJobs.getContent().size()).isEqualTo(2);

        // search for all jobs by userid
        var allJobs = service.search(userId, null, null, unpaged());
        assertThat(allJobs.getContent().size()).isEqualTo(3);
    }

    @Test
    void testSearch_getJobsByUserId() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("First Repair Job", userId, "Engine Diagnostic", "IT1234", "Toyota", "Corolla", CREATED);

        service.createJob(job);

        var jobs = service.search(userId, null, null, unpaged());
        assertThat(jobs.getContent().size()).isEqualTo(1);
    }

    private RepairJob createRepairJob() {
        var userId = UUID.randomUUID().toString();
        var repairDescription = randomAlphabetic(10);
        var licensePlate = randomAlphabetic(7);
        var make = randomAlphabetic(10);
        var model = randomAlphabetic(10);
        return createRepairJob("Test Repair Job", userId, repairDescription, licensePlate, make, model, CREATED);
    }

    private RepairJob createRepairJob(String name, String userId, String repairDescription, String licensePlate, String make, String model, RepairStatus status) {
        return RepairJob.builder()
                .name(name)
                .userId(userId)
                .repairDescription(repairDescription)
                .licensePlate(licensePlate)
                .make(make)
                .model(model)
                .status(status).build();
    }
}