package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.EmployeeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO createTestEmployee(String name) throws Exception {
        CreateEmployeeDTO createEmployeeDTO = CreateEmployeeDTO.builder()
            .name(name)
            .jobTitle("Caretaker")
            .contactInformation("test@example.com")
            .build();

        String response = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeDTO)))
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, EmployeeDTO.class);
    }

    @Test
    void createEmployee_ShouldCreateAndReturnEmployee() throws Exception {
        // Given: A valid CreateEmployeeDTO
        CreateEmployeeDTO createEmployeeDTO = CreateEmployeeDTO.builder()
            .name("John Doe")
            .jobTitle("Caretaker")
            .contactInformation("john.doe@example.com")
            .build();

        // When/Then: Creating a new employee
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.jobTitle").value("Caretaker"))
            .andExpect(jsonPath("$.contactInformation").value("john.doe@example.com"));
    }

    @Test
    void getEmployee_ShouldReturnEmployee() throws Exception {
        // Given
        EmployeeDTO savedEmployee = createTestEmployee("Jane Smith");

        // When/Then
        mockMvc.perform(get("/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.jobTitle").value("Caretaker"))
                .andExpect(jsonPath("$.id").value(savedEmployee.getId()));
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() throws Exception {
        // Given
        createTestEmployee("Employee 1");
        createTestEmployee("Employee 2");
        createTestEmployee("Employee 3");

        // When/Then
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Employee 1", "Employee 2", "Employee 3")));
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployeesWithPagination() throws Exception {
        // Given
        createTestEmployee("John Smith");
        createTestEmployee("Jane Doe");
        createTestEmployee("Bob Wilson");

        // When/Then
        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.content[*].name", hasItems("John Smith", "Jane Doe", "Bob Wilson")))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getAllEmployees_WithCustomPageSize_ShouldReturnCorrectNumberOfEmployees() throws Exception {
        // Given
        createTestEmployee("John Smith");
        createTestEmployee("Jane Doe");
        createTestEmployee("Bob Wilson");

        // When/Then
        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(2)));
    }

    @Test
    void updateEmployee_ShouldUpdateAndReturnEmployee() throws Exception {
        // Given
        EmployeeDTO savedEmployee = createTestEmployee("Original Name");
        savedEmployee.setName("Updated Name");
        savedEmployee.setJobTitle("Senior Caretaker");

        // When/Then
        mockMvc.perform(put("/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.jobTitle").value("Senior Caretaker"))
                .andExpect(jsonPath("$.id").value(savedEmployee.getId()));
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployee() throws Exception {
        // Given
        EmployeeDTO savedEmployee = createTestEmployee("To Delete");

        // When
        mockMvc.perform(delete("/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_WithInvalidData_ShouldReturn400() throws Exception {
        // Given: An invalid CreateEmployeeDTO
        CreateEmployeeDTO invalidEmployee = new CreateEmployeeDTO();
        invalidEmployee.setName(""); // Invalid: empty name
        invalidEmployee.setJobTitle(""); // Invalid: empty job title
        invalidEmployee.setContactInformation("invalid-email"); // Invalid: wrong email format

        // When/Then: Creating with invalid data should fail
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmployee)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors.jobTitle").exists())
            .andExpect(jsonPath("$.errors.contactInformation").exists());
    }

    @Test
    void getAllEmployees_WithSorting_ShouldReturnSortedEmployees() throws Exception {
        // Given
        createTestEmployee("Charlie");
        createTestEmployee("Alice");
        createTestEmployee("Bob");

        // When/Then: Sort by name ascending
        mockMvc.perform(get("/employees")
                .param("sortBy", "name")
                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Charlie"));

        // When/Then: Sort by name descending
        mockMvc.perform(get("/employees")
                .param("sortBy", "name")
                .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Charlie"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Alice"));
    }
}