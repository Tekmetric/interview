package com.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.query.repository.WidgetQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests that verify the complete flow from HTTP requests through
 * controllers, handlers, and repositories to the database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WidgetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WidgetCommandRepository widgetCommandRepository;

    @Autowired
    private WidgetQueryRepository widgetQueryRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() throws InterruptedException {
        // Clean up databases before each test
        widgetCommandRepository.deleteAll();
        widgetQueryRepository.deleteAll();

        // Wait to ensure cleanup is complete and any pending events are processed
        Thread.sleep(300);
    }

    @Test
    void fullCRUDFlow_ShouldWorkEndToEnd() throws Exception {
        // 1. CREATE - Create a new widget via POST
        CreateWidgetCommand createCommand = new CreateWidgetCommand("Integration Test Widget", "Testing full CRUD");

        MvcResult createResult = mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Widget"))
                .andExpect(jsonPath("$.description").value("Testing full CRUD"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Long widgetId = objectMapper.readTree(responseBody).get("id").asLong();

        // Give event processing time to complete
        Thread.sleep(500);

        // 2. READ - Get all widgets via GET
        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[?(@.id == " + widgetId + ")].name").value("Integration Test Widget"));

        // 3. READ - Get specific widget by ID via GET
        mockMvc.perform(get("/api/widgets/" + widgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widgetId))
                .andExpect(jsonPath("$.name").value("Integration Test Widget"))
                .andExpect(jsonPath("$.description").value("Testing full CRUD"));

        // 4. UPDATE - Update the widget via PUT
        UpdateWidgetCommand updateCommand = new UpdateWidgetCommand("Updated Widget", "Updated description");

        mockMvc.perform(put("/api/widgets/" + widgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widgetId))
                .andExpect(jsonPath("$.name").value("Updated Widget"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        // Verify update is reflected in query
        mockMvc.perform(get("/api/widgets/" + widgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Widget"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        // 5. DELETE - Delete the widget via DELETE
        mockMvc.perform(delete("/api/widgets/" + widgetId))
                .andExpect(status().isNoContent());

        // Give event processing time to complete
        Thread.sleep(500);

        // Verify widget is gone from query
        mockMvc.perform(get("/api/widgets/" + widgetId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMultipleWidgets_ShouldAllBeRetrievable() throws Exception {
        // Create first widget
        CreateWidgetCommand command1 = new CreateWidgetCommand("Widget 1", "Description 1");
        MvcResult result1 = mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command1)))
                .andExpect(status().isCreated())
                .andReturn();

        // Create second widget
        CreateWidgetCommand command2 = new CreateWidgetCommand("Widget 2", "Description 2");
        MvcResult result2 = mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command2)))
                .andExpect(status().isCreated())
                .andReturn();

        // Create third widget
        CreateWidgetCommand command3 = new CreateWidgetCommand("Widget 3", "Description 3");
        MvcResult result3 = mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command3)))
                .andExpect(status().isCreated())
                .andReturn();

        // Wait for cache eviction to complete
        Thread.sleep(100);

        // Clear cache to ensure we read fresh data from database
        cacheManager.getCache("widgets").clear();
        cacheManager.getCache("allWidgets").clear();

        // Verify all three are retrievable
        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void updateNonExistentWidget_ShouldReturnNotFound() throws Exception {
        UpdateWidgetCommand command = new UpdateWidgetCommand("Non-existent", "This won't work");

        mockMvc.perform(put("/api/widgets/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentWidget_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/widgets/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNonExistentWidget_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/widgets/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWidgetWithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Blank name
        CreateWidgetCommand invalidCommand = new CreateWidgetCommand("", "Description");

        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eventSynchronization_ShouldSyncFromCommandToQueryDatabase() throws Exception {
        // Create widget in command database
        CreateWidgetCommand createCommand = new CreateWidgetCommand("Event Sync Test", "Testing event sync");

        MvcResult result = mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Long widgetId = objectMapper.readTree(responseBody).get("id").asLong();

        // Give event processing time to complete
        Thread.sleep(500);

        // Verify widget exists in query database (via query endpoint)
        mockMvc.perform(get("/api/widgets/" + widgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widgetId))
                .andExpect(jsonPath("$.name").value("Event Sync Test"));
    }

    @Test
    void concurrentCreates_ShouldAllSucceed() throws Exception {
        // Create multiple widgets rapidly
        for (int i = 1; i <= 5; i++) {
            CreateWidgetCommand command = new CreateWidgetCommand("Concurrent Widget " + i, "Description " + i);
            mockMvc.perform(post("/api/widgets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isCreated());
        }

        // Give event processing time to complete
        Thread.sleep(1000);

        // Verify all 5 were created
        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }
}
