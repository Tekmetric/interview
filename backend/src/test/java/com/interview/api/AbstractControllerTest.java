package com.interview.api;

import com.interview.application.CustomerRepository;
import com.interview.application.VehicleRepository;
import com.interview.application.WorkOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected VehicleRepository vehicleRepository;

    @Autowired
    protected WorkOrderRepository workOrderRepository;

    @AfterEach
    void tearDown() {
        workOrderRepository.findAll(Optional.empty()).forEach(wo -> workOrderRepository.deleteById(wo.getId()));
        vehicleRepository.findAll().forEach(v -> vehicleRepository.deleteById(v.getId()));
        customerRepository.findAll().forEach(c -> customerRepository.deleteById(c.getId()));
    }

    protected abstract String getRequestStubBase();

    protected abstract String getResponseExpectedBase();

    protected String loadJson(String path) {
        try (var in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON: " + path, e);
        }
    }

    protected String loadRequest(String name) {
        return loadJson(getRequestStubBase() + name);
    }

    protected String loadExpected(String name) {
        return loadJson(getResponseExpectedBase() + name);
    }

    protected static String extractIdFromResponse(String responseBody) {
        return responseBody.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    }

    protected static String extractFieldFromJson(String responseBody, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\":\"([^\"]+)\"");
        java.util.regex.Matcher regexMatcher = pattern.matcher(responseBody);
        if (!regexMatcher.find()) {
            throw new IllegalArgumentException("Field not found in JSON: " + fieldName);
        }
        return regexMatcher.group(1);
    }

    protected ResultActions post(String uri, String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    protected ResultActions get(String uri) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(uri));
    }

    protected ResultActions put(String uri, String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    protected ResultActions delete(String uri) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(uri));
    }

    protected String assertCreatedAndGetId(ResultActions result) throws Exception {
        String responseBody = result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return extractIdFromResponse(responseBody);
    }

    protected void assertResponse(ResultActions result, ResultMatcher statusMatcher, String expectedJson) throws Exception {
        result.andExpect(statusMatcher);
        if (expectedJson != null) {
            result.andExpect(content().json(expectedJson, false));
        }
    }
}
