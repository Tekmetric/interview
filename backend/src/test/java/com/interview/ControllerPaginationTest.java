package com.interview;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.interview.model.CustomerDTO;
import com.interview.resource.HttpResource;
import com.interview.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private HttpResource customerController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void testGetPaginatedCustomers() throws Exception {
        // Given
        int page = 0;
        int size = 5;
        Page<CustomerDTO> mockPage = mock(Page.class);
        when(customerService.getCustomers(page, size, "birth_year", "asc")).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/customers")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", "birthYear"))
                .andExpect(status().isOk());

        verify(customerService, times(1)).getCustomers(page, size, "birthYear", "asc");
    }

    @Test
    public void testPaginationWithContent() throws Exception {
        // Given
        int page = 0;
        int size = 5;
        List<CustomerDTO> customers = List.of(
                new CustomerDTO("john@example.com", "John", "Doe", "5 Main Street, Arlington VA", (short) 1990),
                new CustomerDTO("jane@example.com", "Jane", "Doe", "10 Bedford Street, Arlington VA", (short) 1980)
        );
        Page<CustomerDTO> pageResult = new PageImpl<>(customers, PageRequest.of(page, size), customers.size());

        when(customerService.getCustomers(page, size, "birthYear", "asc")).thenReturn(pageResult);

        // When & Then
        mockMvc.perform(get("/customers")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", "birthYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[1].firstName").value("Jane"));
    }
}