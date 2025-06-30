package com.interview.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class ShopResourceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void createAndListShops() throws Exception {
        String json = "{" +
            "\"name\":\"IntTest\"," +
            "\"address\":\"IntAddr\"," +
            "\"numberOfEmployees\":15" +
            "}";

        mvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("IntTest"))
            .andExpect(jsonPath("$.address").value("IntAddr"))
            .andExpect(jsonPath("$.numberOfEmployees").value(15));

        mvc.perform(get("/api/shops")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].name").value("IntTest"))
            .andExpect(jsonPath("$.content[0].numberOfEmployees").value(15));
    }
}
