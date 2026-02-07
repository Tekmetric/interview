package com.interview.service;

import com.interview.Application;
import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.interview.model.RepairStatus.CANCELLED;
import static com.interview.model.RepairStatus.CREATED;
import static com.interview.model.RepairStatus.IN_PROGRESS;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.data.domain.Pageable.unpaged;

@SpringBootTest(classes = Application.class, webEnvironment = NONE)
public class RepairJobServiceTest {

    @Autowired
    private RepairJobService service;

    @Test
    void testGetAllRepairJobs() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("Engine Diagnostic", userId, "engine diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        service.createRepairJob(job);

        var jobs = service.getAllRepairJobs();
        assertThat(jobs.size()).isGreaterThan(1);
    }

    @Test
    void getRepairJobById() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("Engine Diagnostic", userId, "engine diagnostic",
                "IT1234", "Toyota", "Corolla", CREATED);

        var savedJob = service.createRepairJob(job);
        assertThat(service.getRepairJobById(savedJob.getId()))
                .as("Expected job lookup to return saved job")
                .isPresent()
                .get()
                .extracting(RepairJob::getName)
                .isEqualTo(savedJob.getName());
    }

    @Test
    void testCreateRepairJob() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("Engine Diagnostic", userId, "engine diagnostic", "IT1234", "Toyota", "Corolla", CREATED);
        var savedJob = service.createRepairJob(job);

        assertThat(savedJob.getId()).isNotNull();
        assertThat(savedJob.getName()).isEqualTo(job.getName());
        assertThat(savedJob.getUserId()).isEqualTo(job.getUserId());
        assertThat(savedJob.getRepairDescription()).isEqualTo(job.getRepairDescription());
        assertThat(savedJob.getLicensePlate()).isEqualTo(job.getLicensePlate());
        assertThat(savedJob.getMake()).isEqualTo(job.getMake());
        assertThat(savedJob.getModel()).isEqualTo(job.getModel());
        assertThat(savedJob.getStatus()).isEqualTo(CREATED);
    }

    @Test
    void testUpdateRepairJob() {
        var userId = UUID.randomUUID().toString();
        var job = createRepairJob("Engine Diagnostic", userId, "engine diagnostic", "IT1234", "Toyota", "Corolla", CREATED);

        var savedJob = service.createRepairJob(job);

        var createdTime = savedJob.getCreated();
        var originalLastModified = savedJob.getLastModified();

        // update request
        var request = RepairJob.builder()
                .name("Repair Job Update")
                .userId("123")
                .repairDescription("my repair should actually have been...")
                .licensePlate("D12345")
                .make("Chevy")
                .model("Camaro")
                .status(IN_PROGRESS).build();

        service.updateRepairJob(savedJob.getId(), request);

        // now get the job again and check for updates
        var updatedOptional = service.getRepairJobById(savedJob.getId());

        // verify record exists
        assertThat(updatedOptional)
                .as("Expected repair job to exist after update")
                .isPresent();

        // check all fields
        var updated = updatedOptional.get();
        assertThat(updated)
                .extracting(
                        RepairJob::getName,
                        RepairJob::getUserId,
                        RepairJob::getRepairDescription,
                        RepairJob::getLicensePlate,
                        RepairJob::getMake,
                        RepairJob::getModel,
                        RepairJob::getStatus
                )
                .containsExactly(
                        request.getName(),
                        request.getUserId(),
                        request.getRepairDescription(),
                        request.getLicensePlate(),
                        request.getMake(),
                        request.getModel(),
                        request.getStatus()
                );

        // ensure created is unchanged
        assertThat(updated.getCreated()).isEqualTo(createdTime);
        // ensure lastModified was updated
        assertThat(updated.getLastModified()).isAfter(originalLastModified);
    }

    @Test
    void testDeleteJob() {
        var savedRepairJob = service.createRepairJob(createRepairJob());
        var id = savedRepairJob.getId();
        assertThat(id).isNotNull();

        service.deleteRepairJob(id);
        assertThat(service.getRepairJobById(id)).isEqualTo(empty());
    }

    @Test
    void testSearch() {
        var licensePlate = RandomStringUtils.randomAlphabetic(7);
        var licensePlate2 = RandomStringUtils.randomAlphabetic(7);
        var userId = UUID.randomUUID().toString();

        // create jobs
        var job1 = createRepairJob("Engine Diagnostic", userId, "Engine Diagnostic", licensePlate, "Toyota", "Corolla", CREATED);
        var job2 = createRepairJob("Oil Changed", userId, "Oil Change", licensePlate, "Toyota", "Corolla", CANCELLED);
        var job3 = createRepairJob("Oil Changed", userId, "Oil Change", licensePlate2, "Toyota", "Corolla", CANCELLED);

        service.createRepairJob(job1);
        service.createRepairJob(job2);
        service.createRepairJob(job3);

        // search by Created and licensePlate
        var jobsByCreatedAndLicensePlate = service.searchRepairJobs(null, CREATED, licensePlate, unpaged());
        assertThat(jobsByCreatedAndLicensePlate.getContent().size()).isEqualTo(1);

        // search by Cancelled and licensePlate
        var jobsByCancelledAndLicensePlate = service.searchRepairJobs(null, CANCELLED, licensePlate, unpaged());
        assertThat(jobsByCancelledAndLicensePlate.getContent().size()).isEqualTo(1);

        // search by license plate only
        var allLicensePlateJobs = service.searchRepairJobs(null, null, licensePlate, unpaged());
        assertThat(allLicensePlateJobs.getContent().size()).isEqualTo(2);

        // search for all jobs by userid only
        var allJobs = service.searchRepairJobs(userId, null, null, unpaged());
        assertThat(allJobs.getContent().size()).isEqualTo(3);
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