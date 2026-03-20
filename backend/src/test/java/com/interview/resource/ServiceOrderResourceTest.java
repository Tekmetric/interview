package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.dto.ServiceOrderDTO;
import com.interview.model.enums.ServiceOrderStatus;
import com.interview.service.ServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceOrderResource.class)
class ServiceOrderResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceOrderService serviceOrderService;

    private ServiceOrderDTO orderDto;
    private final String VIN = "VIN1234567890ABCD";

    @BeforeEach
    void setUp() {
        orderDto = new ServiceOrderDTO("Brake Inspection", LocalDateTime.now(), ServiceOrderStatus.IN_PROGRESS);
    }

    @Test
    void getOrdersByVehicleAndStatus_ShouldReturnFilteredResults() throws Exception {
        Page<ServiceOrderDTO> pagedResponse = new PageImpl<>(List.of(orderDto));

        when(serviceOrderService.getServiceOrdersByVehicleAndStatus(eq(VIN), eq(ServiceOrderStatus.IN_PROGRESS), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/service/vehicle/{vin}/status/IN_PROGRESS", VIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    void getOrdersByVehicle_ShouldReturnPagedResults() throws Exception {
        Page<ServiceOrderDTO> pagedResponse = new PageImpl<>(List.of(orderDto));

        when(serviceOrderService.getServiceOrdersByVehicle(eq(VIN), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/service/vehicle/{vin}", VIN)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Brake Inspection"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void addOrder_ShouldReturnCreated() throws Exception {
        when(serviceOrderService.addServiceOrder(eq(VIN), any(ServiceOrderDTO.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/service/vehicle/{vin}", VIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Brake Inspection"));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedDto() throws Exception {
        Long id = 100L;
        when(serviceOrderService.updateServiceOrder(eq(id), any(ServiceOrderDTO.class))).thenReturn(orderDto);

        mockMvc.perform(put("/api/service/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk());
    }

    @Test
    void removeOrderFromVehicle_ShouldReturnNoContent() throws Exception {
        Long id = 100L;
        doNothing().when(serviceOrderService).removeServiceOrderFromVehicle(VIN, id);

        mockMvc.perform(delete("/api/service/{id}/vehicle/{vin}", id, VIN))
                .andExpect(status().isNoContent());
    }
}