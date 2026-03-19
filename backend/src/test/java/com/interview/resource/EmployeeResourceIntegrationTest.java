package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CreateEmployeeRequest;
import com.interview.dto.UpdateEmployeeRequest;
import com.interview.entity.EmploymentStatus;
import com.interview.entity.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/employees";

    private String createRequestBody() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFirstname("Jane");
        request.setLastname("Doe");
        request.setHiredDate(LocalDate.of(2024, 1, 15));
        request.setGender(Gender.FEMALE);
        request.setEmploymentStatus(EmploymentStatus.ACTIVE);
        request.setYearlySalary(new BigDecimal("75000.00"));
        return objectMapper.writeValueAsString(request);
    }

    @Nested
    @DisplayName("POST /api/v1/employees")
    class Create {
        @Test
        @DisplayName("returns 201 and created employee with id and version")
        void create_returns201AndBody() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.version").value(0))
                    .andExpect(jsonPath("$.firstname").value("Jane"))
                    .andExpect(jsonPath("$.lastname").value("Doe"))
                    .andExpect(jsonPath("$.gender").value("Female"))
                    .andExpect(jsonPath("$.employmentStatus").value("Active"))
                    .andExpect(jsonPath("$.yearlySalary").value(75000.0));
        }

        @Test
        @DisplayName("returns 400 when required field is missing")
        void create_invalid_returns400() throws Exception {
            String body = "{\"lastname\":\"Doe\",\"hiredDate\":\"2024-01-15\",\"gender\":\"Female\",\"employmentStatus\":\"Active\",\"yearlySalary\":75000}";

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.fieldErrors").isArray())
                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("firstname")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees")
    class List {
        @Test
        @DisplayName("returns 200 and paginated content")
        void list_returns200AndPage() throws Exception {
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").isNumber())
                    .andExpect(jsonPath("$.totalPages").isNumber())
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.number").value(0));
        }

        @Test
        @DisplayName("respects page and size params")
        void list_withParams_returnsPage() throws Exception {
            mockMvc.perform(get(BASE_URL + "?page=0&size=5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.number").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees/{id}")
    class GetById {
        @Test
        @DisplayName("returns 200 and employee when exists")
        void getById_whenExists_returns200() throws Exception {
            String createBody = createRequestBody();
            String responseBody = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            Long id = objectMapper.readTree(responseBody).get("id").asLong();

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.firstname").value("Jane"));
        }

        @Test
        @DisplayName("returns 404 when not found")
        void getById_whenNotExists_returns404() throws Exception {
            mockMvc.perform(get(BASE_URL + "/99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(containsString("99999")));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/employees/{id}")
    class Update {
        @Test
        @DisplayName("returns 200 and updated employee when exists")
        void update_whenExists_returns200() throws Exception {
            String createBody = createRequestBody();
            String responseBody = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            Long id = objectMapper.readTree(responseBody).get("id").asLong();

            UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest();
            updateRequest.setFirstname("Janet");
            updateRequest.setYearlySalary(new BigDecimal("80000.00"));
            String updateBody = objectMapper.writeValueAsString(updateRequest);

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstname").value("Janet"))
                    .andExpect(jsonPath("$.yearlySalary").value(80000.0))
                    .andExpect(jsonPath("$.lastname").value("Doe"));
        }

        @Test
        @DisplayName("returns 404 when not found")
        void update_whenNotExists_returns404() throws Exception {
            String body = "{\"firstname\":\"X\"}";
            mockMvc.perform(put(BASE_URL + "/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/employees/{id}")
    class Delete {
        @Test
        @DisplayName("returns 204 when exists")
        void delete_whenExists_returns204() throws Exception {
            String createBody = createRequestBody();
            String responseBody = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            Long id = objectMapper.readTree(responseBody).get("id").asLong();

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void delete_whenNotExists_returns404() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/99999"))
                    .andExpect(status().isNotFound());
        }
    }
}
