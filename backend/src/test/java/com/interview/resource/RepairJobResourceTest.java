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
    void testSuccess() throws Exception {
        mockMvc.perform(get("/api/repair-jobs"))
                .andExpect(status().isOk());
    }

    @Test
    void testNonExistentEndpoint() throws Exception {
        mockMvc.perform(get("/api/repair-jobs/notfound"))
                .andExpect(status().is4xxClientError());
    }
}
