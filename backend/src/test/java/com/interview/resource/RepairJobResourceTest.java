package com.interview.resource;

import com.interview.model.RepairJob;
import com.interview.model.RepairStatus;
import com.interview.service.RepairJobService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepairJobResource.class)
public class RepairJobResourceTest {

    private final MockMvc mockMvc;

    @Autowired
    public RepairJobResourceTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockitoBean private RepairJobService service;

    @Test
    @SneakyThrows
    void testPostSuccess() {
        var saved = new RepairJob();
        saved.setId(15L);
        saved.setUserId("user-123");
        saved.setLicensePlate("ABC1234");
        saved.setMake("Toyota");
        saved.setModel("Corolla");
        saved.setStatus(RepairStatus.CREATED);

        when(service.createJob(any())).thenReturn(saved);

        var requestJson = """
            {
              "jobName": "First Repair Job",
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
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void testGetSuccess() {
        var job = new RepairJob();
        job.setId(15L);
        job.setUserId("user-123");
        job.setLicensePlate("ABC1234");
        job.setMake("Toyota");
        job.setModel("Corolla");
        job.setStatus(RepairStatus.CREATED);

        when(service.getJobById(15L)).thenReturn(Optional.of(job));

        mockMvc.perform(get("/api/repair-jobs/15"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testPutSuccess() {
        var updated = new RepairJob();
        updated.setId(15L);
        updated.setUserId("user-123");
        updated.setLicensePlate("ZZZ9999");
        updated.setMake("Toyota");
        updated.setModel("Corolla");
        updated.setStatus(RepairStatus.IN_PROGRESS);

        when(service.updateJob(eq(15L), any())).thenReturn(updated);

        var requestJson = """
        {
          "jobName": "First Repair Job",
              "userId": "user-123",
              "licensePlate": "ABC1234",
              "repairDescription": "repair description",
              "make": "Toyota",
              "model": "Corolla",
              "status": "CREATED"
        }
        """;
        mockMvc.perform(put("/api/repair-jobs/15")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testDeleteSuccess() {
        mockMvc.perform(delete("/api/repair-jobs/100"))
                .andExpect(status().isNoContent());
        verify(service).deleteJob(any());
    }

    @Test
    @SneakyThrows
    void testGetInvalidId() {
        mockMvc.perform(get("/api/repair-jobs/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testGetInvalidEndpoint() {
        mockMvc.perform(get("/jobs/1"))
                .andExpect(status().isInternalServerError());
    }
}
