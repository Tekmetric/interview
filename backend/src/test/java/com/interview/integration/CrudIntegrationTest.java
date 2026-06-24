package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.Part;
import com.interview.util.PartOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.interview.util.UtilityMethods.fromMvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Resets DB state after each test
public class CrudIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    PartOperations partOperations;

    @BeforeEach
    void setUp() {
        partOperations = new PartOperations(mvc, objectMapper); // Included in setup call to ensure MockMvc is initialized
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getPart_shouldReturnPart() throws Exception {
        mvc.perform(get("/api/part/{partId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Bolt M8 x 30mm\",\"inventory\":200}"));
    }

    @Test
    void deletePart_shouldFail_whenPartIsUsedByWorkOrder() throws Exception {
        mvc.perform(delete("/api/part/{partId}", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUpdateDeletePart() throws Exception {
        var newPart = partOperations.createPart(new Part().setName("Doodad"));

        assertThat(newPart.getName()).isEqualTo("Doodad");
        assertThat(newPart.getInventory()).isEqualTo(0);

        var partId = newPart.getId();

        partOperations.updatePart(partId, new Part().setName("Thing-a-ma-bob"));

        var updatedPart = partOperations.getPart(partId);

        assertThat(updatedPart.getName()).isEqualTo("Thing-a-ma-bob");

        partOperations.deletePart(partId);

        mvc.perform(get("/api/part/{partId}", partId))
                .andExpect(status().isNotFound()); // After deletion, part should no longer be found
    }
}
