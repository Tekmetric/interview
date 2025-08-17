package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CreateCustomerRequest;
import com.interview.dto.CustomerResponse;
import com.interview.dto.PagedResponse;
import com.interview.entity.Customer;
import com.interview.entity.Role;
import com.interview.service.CustomerService;
import com.interview.service.EventPublisher;
import com.interview.config.CustomerConfig;
import com.interview.mapper.CustomerMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerConfig customerConfig;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;
    private CustomerResponse testCustomerResponse;

    @BeforeEach
    void setUp() {
        // Create a MockMvc instance that's directly wired to the CustomerController instance.
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();

        // Set up a soccer player customer
        testCustomer = Customer.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .firstName("Lionel")
                .lastName("Messi")
                .email("lmessi@psg.com")
                .password("hashedPassword")
                .role(Role.USER)
                .version(1)
                .createdAt(LocalDateTime.now())
                .build();

        // Set up a soccer player customerResponse
        testCustomerResponse = new CustomerResponse();
        testCustomerResponse.setId(testCustomer.getId().toString());
        testCustomerResponse.setFirstName("Lionel");
        testCustomerResponse.setLastName("Messi");
        testCustomerResponse.setEmail("lmessi@psg.com");
        testCustomerResponse.setVersion(1);
        testCustomerResponse.setCreatedAt(testCustomer.getCreatedAt());
    }

    @Test
    public void shouldGetCustomersWithPaginationAndSorting() throws Exception {
        // Service returns a paged response with customers
        PagedResponse<CustomerResponse> expectedResponse = new PagedResponse<>();
        expectedResponse.setContent(List.of(testCustomerResponse));

        when(customerService.getCustomers("lastName", 0, 10, "Messi", "Lionel"))
                .thenReturn(expectedResponse);

        // GET request to /api/customers with pagination and filter parameters
        mockMvc.perform(get("/api/customers")
                        .param("sort", "lastName")
                        .param("page", "0")
                        .param("size", "10")
                        .param("lastname", "Messi")
                        .param("firstname", "Lionel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customer_id").value(testCustomer.getId().toString()))
                .andExpect(jsonPath("$.content[0].firstName").value("Lionel"))
                .andExpect(jsonPath("$.content[0].lastName").value("Messi"));

        // Verify service was called with correct parameters
        verify(customerService).getCustomers("lastName", 0, 10, "Messi", "Lionel");
    }

    @Test
    public void shouldCreateCustomerSuccessfully() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFirstName("Cristiano");
        request.setLastName("Ronaldo");
        request.setEmail("cr7@alnassr.com");
        request.setPassword("siuuuu123");
        request.setRole(Role.USER);

        // Create a separate customer for this test
        Customer createdCustomer = Customer.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .firstName("Cristiano")
                .lastName("Ronaldo")
                .email("cr7@alnassr.com")
                .password("hashedPassword")
                .role(Role.USER)
                .version(1)
                .createdAt(LocalDateTime.now())
                .build();

        CustomerResponse createdCustomerResponse = new CustomerResponse();
        createdCustomerResponse.setId(createdCustomer.getId().toString());
        createdCustomerResponse.setFirstName("Cristiano");
        createdCustomerResponse.setLastName("Ronaldo");
        createdCustomerResponse.setEmail("cr7@alnassr.com");
        createdCustomerResponse.setVersion(1);
        createdCustomerResponse.setCreatedAt(createdCustomer.getCreatedAt());

        when(customerConfig.getThreshold()).thenReturn(3);
        when(customerService.countByLastName("Ronaldo")).thenReturn(0L);
        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(createdCustomer);
        when(customerMapper.toDto(createdCustomer)).thenReturn(createdCustomerResponse);

        // POST request should create customer and return 201 Created
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customer_id").value(createdCustomer.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("Cristiano"))
                .andExpect(jsonPath("$.lastName").value("Ronaldo"))
                .andExpect(jsonPath("$.email").value("cr7@alnassr.com"));

        // Verify service interactions
        verify(customerService).createCustomer(any(CreateCustomerRequest.class));
        verify(eventPublisher).publishCustomerCreatedEvent(createdCustomer);
    }

    @Test
    public void shouldGetCustomerByIdSuccessfully() throws Exception {
        // Service returns customer
        UUID customerId = testCustomer.getId();
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerResponse);

        // GET request should return customer data
        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(customerId.toString()))
                .andExpect(jsonPath("$.firstName").value("Lionel"))
                .andExpect(jsonPath("$.lastName").value("Messi"))
                .andExpect(jsonPath("$.email").value("lmessi@psg.com"));

        verify(customerService).findCustomerById(customerId);
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    public void shouldReturn404WhenCustomerNotFound() throws Exception {
        // Service returns empty for non-existent customer
        UUID nonExistentId = UUID.randomUUID();
        when(customerService.findCustomerById(nonExistentId)).thenReturn(Optional.empty());

        // Should return 404 Not Found
        mockMvc.perform(get("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(customerService).findCustomerById(nonExistentId);
        // Mapper should not be called when customer is not found
        verify(customerMapper, never()).toDto(any(Customer.class));
    }

    @Test
    public void shouldDeleteCustomerSuccessfully() throws Exception {
        // GIVEN: Customer exists
        UUID customerId = testCustomer.getId();
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        // WHEN & THEN: DELETE request should remove customer
        mockMvc.perform(delete("/api/customers/{customer_id}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService).findCustomerById(customerId);
        verify(customerService).deleteCustomer(testCustomer);
    }
}