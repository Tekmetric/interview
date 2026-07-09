package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehiclePatchRequest;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.GlobalExceptionHandler;
import com.interview.exception.VehicleNotFoundException;
import com.interview.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleResource.class)
@Import(GlobalExceptionHandler.class)
class VehicleResourceTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private VehicleService service;

    private static VehicleResponse sampleResponse(long id) {
        return new VehicleResponse(id, "1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000L,
                Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Test
    void postReturns201WithLocation() throws Exception {
        VehicleRequest req = new VehicleRequest("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000L);
        when(service.create(any())).thenReturn(sampleResponse(1L));

        mvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/vehicles/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("1HGCM82633A004352"));
    }

    @Test
    void postRejectsInvalidVin() throws Exception {
        VehicleRequest req = new VehicleRequest("too-short", "Honda", "Accord", 2021, "ABC1234", 42000L);

        mvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.vin").exists());
    }

    @Test
    void postRejectsYearBelow1900() throws Exception {
        VehicleRequest req = new VehicleRequest("1HGCM82633A004352", "Honda", "Accord", 1800, "ABC1234", 42000L);

        mvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.year").exists());
    }

    @Test
    void getMissingReturns404ProblemDetail() throws Exception {
        when(service.get(999L)).thenThrow(new VehicleNotFoundException(999L));

        mvc.perform(get("/api/v1/vehicles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(containsString("999")));
    }

    @Test
    void updateAcceptsPartialBody() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(sampleResponse(1L));
        String body = "{\"mileage\": 50000}";

        mvc.perform(patch("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(service).update(eq(1L), any(VehiclePatchRequest.class));
    }

    @Test
    void updateRejectsInvalidVinWhenProvided() throws Exception {
        String body = "{\"vin\": \"too-short\"}";

        mvc.perform(patch("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.vin").exists());
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(delete("/api/v1/vehicles/1")).andExpect(status().isNoContent());
        verify(service).delete(1L);
    }

    @Test
    void deleteMissingStillReturns204() throws Exception {
        // DELETE is response-idempotent;
        mvc.perform(delete("/api/v1/vehicles/7")).andExpect(status().isNoContent());
        verify(service).delete(7L);
    }
}
