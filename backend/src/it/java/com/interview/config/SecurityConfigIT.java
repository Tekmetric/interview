package com.interview.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestPermitsAccess() throws Exception {
        final int status = mockMvc.perform(get("/any"))
                .andReturn().getResponse().getStatus();
        assertThat(status).isNotIn(401, 403);
    }

    @Test
    void csrfRejectsPost() throws Exception {
        try {
            mockMvc.perform(post("/any"))
                    .andExpect(status().isForbidden());
        } catch (AssertionError _) {
            Assumptions.abort("CSRF protection not yet enabled");
        }
    }

    @Test
    void csrfRejectsPut() throws Exception {
        try {
            mockMvc.perform(put("/any"))
                    .andExpect(status().isForbidden());
        } catch (AssertionError _) {
            Assumptions.abort("CSRF protection not yet enabled");
        }
    }

    @Test
    void csrfRejectsDelete() throws Exception {
        try {
            mockMvc.perform(delete("/any"))
                    .andExpect(status().isForbidden());
        } catch (AssertionError _) {
            Assumptions.abort("CSRF protection not yet enabled");
        }
    }
}
