package com.interview.controller;

import com.interview.model.JobStatus;
import com.interview.model.db.Car;
import com.interview.model.db.Job;
import com.interview.model.dto.JobCreateRequest;
import com.interview.model.dto.JobResponse;
import com.interview.model.dto.JobUpdateRequest;
import com.interview.repository.CarRepository;
import com.interview.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Activates application-test.properties
@AutoConfigureWebTestClient
public class JobControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CarRepository carRepository;

    @Test
    void addNewJobForNewCar() {

        JobCreateRequest jobRequest = new JobCreateRequest(null,
                "ABC123DEF456GHI789",
                "Chevrolet",
                "Bolt",
                2021,
                "John Doe",
                Instant.now());

        JobResponse response = webClient
                .post().uri("/api/v1/jobs")
                .bodyValue(jobRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(JobResponse.class).returnResult().getResponseBody();
        assertEquals(response, jobRequest);

        // Check new car was added to db and we have audit fields set
        Car car = carRepository.findCarByVin(jobRequest.vin()).orElseThrow();
        assertEquals(car, jobRequest);
        checkAuditColumnsCreated(car.getCreatedAt(), car.getUpdatedAt());

        // Check job was added to db and we have audit fields set
        Job job = jobRepository.findById(response.id()).orElseThrow();
        assertEquals(job, jobRequest);
        checkAuditColumnsCreated(job.getCreatedAt(), job.getUpdatedAt());
    }

    @Test
    void notFoundOnUpdateOrDeleteWithNoJob() throws Exception {
        JobUpdateRequest request = new JobUpdateRequest(JobStatus.IN_PROGRESS, Instant.now());

        webClient
                .put().uri("/api/v1/jobs/1234")
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();

        webClient
                .delete().uri("/api/v1/jobs/1234")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void badRequestOnEmptyOrNullMandatoryFields() {
        // TODO
    }

    @Test
    void createNewJobForExistingCar() {
        // TODO
    }

    @Test
    void updateJob() {
        // TODO
    }

    @Test
    void deleteJob() {
        // TODO
    }

    void assertEquals(JobResponse response, JobCreateRequest request) {
        assertThat(response).isNotNull();
        assertThat(response.vin()).isEqualTo(request.vin());
        assertThat(response.customer()).isEqualTo(request.customer());
        assertThat(response.make()).isEqualTo(request.make());
        assertThat(response.model()).isEqualTo(request.model());
        assertThat(response.modelYear()).isEqualTo(request.modelYear());
        assertThat(response.scheduledAt()).isEqualTo(request.scheduledAt());
        assertThat(response.status()).isEqualTo(JobStatus.SCHEDULED);
    }

    void assertEquals(Car car, JobCreateRequest request) {
        assertThat(car).isNotNull();
        assertThat(car.getVin()).isEqualTo(request.vin());
        assertThat(car.getCustomer()).isEqualTo(request.customer());
        assertThat(car.getMake()).isEqualTo(request.make());
        assertThat(car.getModel()).isEqualTo(request.model());
        assertThat(car.getModelYear()).isEqualTo(request.modelYear());
        assertThat(car.getCreatedAt()).isNotNull();
        assertThat(car.getUpdatedAt()).isNotNull();
    }

    void assertEquals(Job job, JobCreateRequest request) {
        assertThat(job).isNotNull();
        assertThat(job.getScheduledAt()).isCloseTo(request.scheduledAt(), within(1, ChronoUnit.MILLIS));
        assertThat(job.getStatus()).isEqualTo(JobStatus.SCHEDULED);
    }

    void checkAuditColumnsCreated(Instant createdAt, Instant updatedAt) {
        assertThat(createdAt).isNotNull();
        assertThat(updatedAt).isNotNull();
        assertThat(createdAt).isCloseTo(updatedAt, within(1, ChronoUnit.MILLIS));
    }

}
