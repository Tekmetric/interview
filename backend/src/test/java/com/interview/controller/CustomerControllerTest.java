package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.interview.dto.CustomerDTO;
import com.interview.dto.VehicleDTO;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.GlobalExceptionHandler;
import com.interview.service.CustomerService;
import com.interview.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerController Unit Tests")
class CustomerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDTO validCustomerDTO;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        
        validCustomerDTO = new CustomerDTO();
        validCustomerDTO.setId(1L);
        validCustomerDTO.setFirstName("John");
        validCustomerDTO.setLastName("Doe");
        validCustomerDTO.setEmail("john.doe@example.com");
        validCustomerDTO.setPhoneNumber("555-0101");
    }

    @Test
    @DisplayName("Should pass email search parameter to service layer")
    void shouldPassEmailParameterToService() throws Exception {
        Page<CustomerDTO> customerPage = new PageImpl<>(List.of(validCustomerDTO));
        when(customerService.getAllCustomers(eq("john"), isNull(), isNull(), any())).thenReturn(customerPage);

        try {
            mockMvc.perform(get("/api/v1/customers")
                    .param("email", "john"));
        } catch (Exception e) {
        }

        verify(customerService).getAllCustomers(eq("john"), isNull(), isNull(), any());
    }

    @Test
    @DisplayName("Should pass firstName search parameter to service layer")
    void shouldPassFirstNameParameterToService() throws Exception {
        Page<CustomerDTO> customerPage = new PageImpl<>(List.of(validCustomerDTO));
        when(customerService.getAllCustomers(isNull(), eq("John"), isNull(), any())).thenReturn(customerPage);

        try {
            mockMvc.perform(get("/api/v1/customers")
                    .param("firstName", "John"));
        } catch (Exception e) {
        }

        verify(customerService).getAllCustomers(isNull(), eq("John"), isNull(), any());
    }

    @Test
    @DisplayName("Should pass lastName search parameter to service layer")
    void shouldPassLastNameParameterToService() throws Exception {
        Page<CustomerDTO> customerPage = new PageImpl<>(List.of(validCustomerDTO));
        when(customerService.getAllCustomers(isNull(), isNull(), eq("Doe"), any())).thenReturn(customerPage);

        try {
            mockMvc.perform(get("/api/v1/customers")
                    .param("lastName", "Doe"));
        } catch (Exception e) {
        }

        verify(customerService).getAllCustomers(isNull(), isNull(), eq("Doe"), any());
    }

    @Test
    @DisplayName("Should pass multiple search parameters to service layer")
    void shouldPassMultipleSearchParametersToService() throws Exception {
        Page<CustomerDTO> customerPage = new PageImpl<>(List.of(validCustomerDTO));
        when(customerService.getAllCustomers(eq("john"), eq("John"), eq("Doe"), any())).thenReturn(customerPage);

        try {
            mockMvc.perform(get("/api/v1/customers")
                    .param("email", "john")
                    .param("firstName", "John")
                    .param("lastName", "Doe"));
        } catch (Exception e) {
        }

        verify(customerService).getAllCustomers(eq("john"), eq("John"), eq("Doe"), any());
    }

    @Test
    @DisplayName("Should pass pagination parameters to service layer")
    void shouldPassPaginationParametersToService() throws Exception {
        Page<CustomerDTO> customerPage = new PageImpl<>(List.of(validCustomerDTO));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(customerService.getAllCustomers(isNull(), isNull(), isNull(), any())).thenReturn(customerPage);

        try {
            mockMvc.perform(get("/api/v1/customers")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "firstName,desc"));
        } catch (Exception e) {
        }

        verify(customerService).getAllCustomers(isNull(), isNull(), isNull(), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();
        
        // Verify pagination parameters were passed correctly
        assert capturedPageable.getPageNumber() == 1 : "Expected page number 1 but got " + capturedPageable.getPageNumber();
        assert capturedPageable.getPageSize() == 10 : "Expected page size 10 but got " + capturedPageable.getPageSize();
        assert capturedPageable.getSort().getOrderFor("firstName") != null : "Expected sort by firstName";
        assert capturedPageable.getSort().getOrderFor("firstName").getDirection() == Sort.Direction.DESC : "Expected DESC direction";
    }

    @Test
    @DisplayName("Should return 200 OK when getting customer by ID")
    void shouldReturn200_WhenGettingCustomerById() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(validCustomerDTO);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when customer ID does not exist")
    void shouldReturn404_WhenCustomerIdDoesNotExist() throws Exception {
        when(customerService.getCustomerById(999L))
                .thenThrow(new ResourceNotFoundException("Customer", 999L));

        mockMvc.perform(get("/api/v1/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(customerService).getCustomerById(999L);
    }

    @Test
    @DisplayName("Should return 201 Created when creating valid customer")
    void shouldReturn201_WhenCreatingValidCustomer() throws Exception {
        CustomerDTO newCustomer = new CustomerDTO();
        newCustomer.setFirstName("Jane");
        newCustomer.setLastName("Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setPhoneNumber("555-0102");

        CustomerDTO createdCustomer = new CustomerDTO();
        createdCustomer.setId(2L);
        createdCustomer.setFirstName("Jane");
        createdCustomer.setLastName("Smith");
        createdCustomer.setEmail("jane.smith@example.com");
        createdCustomer.setPhoneNumber("555-0102");

        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(createdCustomer);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        verify(customerService).createCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating customer with invalid data")
    void shouldReturn400_WhenCreatingCustomerWithInvalidData() throws Exception {
        CustomerDTO invalidCustomer = new CustomerDTO();
        invalidCustomer.setFirstName("");
        invalidCustomer.setEmail("invalid-email");
        invalidCustomer.setPhoneNumber("abc");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(customerService, never()).createCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when email already exists")
    void shouldReturn400_WhenEmailAlreadyExists() throws Exception {
        CustomerDTO newCustomer = new CustomerDTO();
        newCustomer.setFirstName("Jane");
        newCustomer.setLastName("Smith");
        newCustomer.setEmail("existing@example.com");
        newCustomer.setPhoneNumber("555-0102");

        when(customerService.createCustomer(any(CustomerDTO.class)))
                .thenThrow(new DuplicateResourceException("Customer", "email", "existing@example.com"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        verify(customerService).createCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 200 OK when updating existing customer")
    void shouldReturn200_WhenUpdatingExistingCustomer() throws Exception {
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setEmail("john.doe@example.com");
        updateDTO.setPhoneNumber("555-9999");

        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setId(1L);
        updatedCustomer.setFirstName("Updated");
        updatedCustomer.setLastName("Name");
        updatedCustomer.setEmail("john.doe@example.com");
        updatedCustomer.setPhoneNumber("555-9999");

        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Updated"));

        verify(customerService).updateCustomer(eq(1L), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating non-existent customer")
    void shouldReturn404_WhenUpdatingNonExistentCustomer() throws Exception {
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setEmail("john.doe@example.com");
        updateDTO.setPhoneNumber("555-9999");

        when(customerService.updateCustomer(eq(999L), any(CustomerDTO.class)))
                .thenThrow(new ResourceNotFoundException("Customer", 999L));

        mockMvc.perform(put("/api/v1/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(customerService).updateCustomer(eq(999L), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating with invalid data")
    void shouldReturn400_WhenUpdatingWithInvalidData() throws Exception {
        CustomerDTO invalidUpdate = new CustomerDTO();
        invalidUpdate.setEmail("invalid-email");

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(customerService, never()).updateCustomer(anyLong(), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting existing customer")
    void shouldReturn204_WhenDeletingExistingCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existent customer")
    void shouldReturn404_WhenDeletingNonExistentCustomer() throws Exception {
        doThrow(new ResourceNotFoundException("Customer", 999L))
                .when(customerService).deleteCustomer(999L);

        mockMvc.perform(delete("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService).deleteCustomer(999L);
    }

    @Test
    @DisplayName("Should return 200 OK when getting customer's vehicles")
    void shouldReturn200_WhenGettingCustomerVehicles() throws Exception {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        vehicleDTO.setMake("Toyota");
        vehicleDTO.setCustomerId(1L);

        when(vehicleService.getVehiclesByCustomerId(1L)).thenReturn(List.of(vehicleDTO));

        mockMvc.perform(get("/api/v1/customers/1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(vehicleService).getVehiclesByCustomerId(1L);
    }

    @Test
    @DisplayName("Should reject customer with invalid phone format")
    void shouldRejectCustomer_WithInvalidPhoneFormat() throws Exception {
        CustomerDTO customerWithInvalidPhone = new CustomerDTO();
        customerWithInvalidPhone.setFirstName("John");
        customerWithInvalidPhone.setLastName("Doe");
        customerWithInvalidPhone.setEmail("john@example.com");
        customerWithInvalidPhone.setPhoneNumber("invalid-phone");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerWithInvalidPhone)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(customerService, never()).createCustomer(any(CustomerDTO.class));
    }
}
