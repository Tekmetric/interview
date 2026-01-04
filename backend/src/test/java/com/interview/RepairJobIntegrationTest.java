package com.interview.resource;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.repository.RepairJobRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RepairJobIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RepairJobRepository repository;

    @Test
    @SneakyThrows
    void testCreateRepairJob() {

        var requestJson = """
            {
              "jobName": "First Test Job",
              "userId": "user-123",
              "licensePlate": "ABC1234",
              "repairDescription": "repair description",
              "make": "Toyota",
              "model": "Corolla",
              "status": "CREATED"
            }
        """;
        mockMvc.perform(post("/api/repair-jobs")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.licensePlate").value("ABC1234"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @SneakyThrows
    void testGetRepairJobById() {

        var job = new RepairJob();
        job.setJobName("New Job");
        job.setUserId("user-123");
        job.setLicensePlate("XYZ000");
        job.setMake("Honda");
        job.setModel("Civic");
        job.setRepairDescription("repair");
        job.setStatus(RepairStatus.CREATED);

        var saved = repository.save(job);

        mockMvc.perform(get("/api/repair-jobs/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.licensePlate").value("XYZ000"));
    }

    @Test
    @SneakyThrows
    void testRepairJobMissing() {

        mockMvc.perform(get("/api/repair-jobs/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdateRepairJob() {

        var job = new RepairJob();
        job.setJobName("New Job");
        job.setUserId("user-123");
        job.setLicensePlate("AAA111");
        job.setMake("Ford");
        job.setModel("Focus");
        job.setRepairDescription("repair");
        job.setStatus(RepairStatus.CREATED);

        var saved = repository.save(job);

        var updateJson = """
            {
             "jobName": "First Test Job",
              "userId": "user-123",
              "licensePlate": "BBB222",
              "repairDescription": "repair description",
              "make": "Ford",
              "model": "Focus",
              "status": "IN_PROGRESS"
            }
        """;

        mockMvc.perform(put("/api/repair-jobs/" + saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("BBB222"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        assertThat(repository.findById(saved.getId()))
                .get()
                .extracting(RepairJob::getLicensePlate)
                .isEqualTo("BBB222");
    }

    @Test
    @SneakyThrows
    void testDeleteRepairJob() {

        var job = new RepairJob();
        job.setJobName("New Job");
        job.setUserId("user-123");
        job.setLicensePlate("ABC1234");
        job.setMake("Toyota");
        job.setModel("Camry");
        job.setRepairDescription("repair");
        job.setStatus(RepairStatus.CREATED);

        var saved = repository.save(job);

        mockMvc.perform(delete("/api/repair-jobs/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
