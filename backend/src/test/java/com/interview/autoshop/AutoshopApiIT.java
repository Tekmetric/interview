package com.interview.autoshop;

import static com.interview.autoshop.AutoshopResponseAssert.assertThatResponse;
import static com.interview.autoshop.AutoshopTestFixtures.SEED_FIRST_ID;
import static com.interview.autoshop.AutoshopTestFixtures.SEED_FIRST_NAME;
import static com.interview.autoshop.AutoshopTestFixtures.SEED_MISSING_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.autoshop.dto.AutoshopResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutoshopApiIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void get_by_id_returns_seed_row() throws Exception {
        MvcResult r = mvc.perform(get("/api/autoshops/" + SEED_FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SEED_FIRST_NAME))
                .andReturn();

        AutoshopResponse resp =
                json.readValue(r.getResponse().getContentAsString(), AutoshopResponse.class);
        assertThatResponse(resp)
                .hasId()
                .hasName(SEED_FIRST_NAME)
                .hasTimestamps();
    }

    @Test
    void get_by_id_missing_returns_problem_404() throws Exception {
        mvc.perform(get("/api/autoshops/" + SEED_MISSING_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/problems/not-found"))
                .andExpect(jsonPath("$.title").value("Autoshop not found"));
    }

    @Test
    void lists_seeded_shops() throws Exception {
        mvc.perform(get("/api/autoshops").param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].name").value(SEED_FIRST_NAME))
                .andExpect(jsonPath("$.content[1].name").value("Maple St Auto"))
                .andExpect(jsonPath("$.content[2].name").value("Gulf Coast Repair"));
    }

    @Test
    void list_with_unknown_sort_property_returns_problem_400() throws Exception {
        mvc.perform(get("/api/autoshops").param("sort", "doesNotExist,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/problems/invalid-query-parameter"))
                .andExpect(jsonPath("$.title").value("Invalid query parameter"))
                .andExpect(jsonPath("$.property").value("doesNotExist"));
    }
}
