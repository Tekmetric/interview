package com.interview.resource;

import com.interview.model.RepairJob;
import com.interview.service.RepairJobService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.interview.model.RepairStatus.CREATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
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
        var job = RepairJob.builder()
                .id(15L)
                .userId("user-123")
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Corolla")
                .repairDescription("repair")
                .status(CREATED)
                .build();

        when(service.createJob(any())).thenReturn(job);

        var requestJson = """
            {
              "name": "First Repair Job",
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
        var job = RepairJob.builder()
                .id(15L)
                .userId("user-123")
                .licensePlate("ABC1234")
                .make("Toyota")
                .model("Corolla")
                .repairDescription("repair")
                .status(CREATED)
                .build();

        when(service.getJobById(15L)).thenReturn(Optional.of(job));

        mockMvc.perform(get("/api/repair-jobs/15"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testPutSuccess() {
        var request = RepairJob.builder()
                .id(15L)
                .userId("user-123")
                .licensePlate("ZZZ9999")
                .make("Toyota")
                .model("Corolla")
                .repairDescription("repair")
                .status(CREATED)
                .build();

        when(service.updateJob(eq(15L), any())).thenReturn(request);

        var requestJson = """
        {
          "name": "First Repair Job",
          "userId": "user-123",
          "licensePlate": "ABC1234",
          "repairDescription": "repair description",
          "make": "Toyota",
          "model": "Corolla",
          "status": "IN_PROGRESS"
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
