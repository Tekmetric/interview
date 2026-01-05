package com.interview.resource;

import com.interview.exception.RepairJobNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

        var requestJson = """
            {
              "name": "Repair Job #1",
              "userId": "user-123",
              "licensePlate": "ABC1234",
              "repairDescription": "repair description",
              "make": "Toyota",
              "model": "Corolla",
              "status": "CREATED"
            }
            """;

        when(service.createRepairJob(any())).thenReturn(job);

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

        when(service.getRepairJobById(15L)).thenReturn(Optional.of(job));

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

        var requestJson = """
        {
          "name": "Repair Job Update",
          "userId": "user-123",
          "licensePlate": "ABC1234",
          "repairDescription": "repair description",
          "make": "Toyota",
          "model": "Corolla",
          "status": "IN_PROGRESS"
        }
        """;
        when(service.updateRepairJob(eq(15L), any())).thenReturn(request);

        mockMvc.perform(put("/api/repair-jobs/15")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testPutFailure() {
        var requestJson = """
        {
          "name": "Repair Job Update",
          "userId": "user-123",
          "licensePlate": "ABC1234",
          "repairDescription": "repair description",
          "make": "Toyota",
          "model": "Corolla",
          "status": "IN_PROGRESS"
        }
        """;

        when(service.updateRepairJob(eq(15L), any())).thenThrow(RepairJobNotFoundException.class);

        mockMvc.perform(put("/api/repair-jobs/15")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testDeleteSuccess() {
        mockMvc.perform(delete("/api/repair-jobs/100"))
                .andExpect(status().isNoContent());
        verify(service).deleteRepairJob(any());
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
