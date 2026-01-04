package com.interview.resource;

import com.interview.service.RepairJobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void test_getJob_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/repair-jobs/99999"))
                .andExpect(status().isNotFound())
                .andExpect(status().is4xxClientError());
    }


    /*@Test
    void testSuccess() throws Exception {
        String userId = UUID.randomUUID().toString();
        var job3 = new RepairJob();
        job3.setJobName("Job 3");
        job3.setUserId(userId);
        job3.setRepairDescription("Tire rotation");
        job3.setLicensePlate("XYZ9876");
        job3.setMake("Honda");
        job3.setModel("Civic");
        job3.setCreated(LocalDate.now());
        job3.setLastModified(LocalDate.now());
        job3.setStatus(RepairStatus.COMPLETED);

        repository.save(job3);
        *//*mockMvc.perform(get("/api/repair-jobs"))
                .andExpect(status().isOk());*//*

        mockMvc.perform(get("/api/repair-jobs")
                .param("userId", userId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Page wrapper
                .andExpect(jsonPath("$.totalElements").value(1));
    }*/
}
