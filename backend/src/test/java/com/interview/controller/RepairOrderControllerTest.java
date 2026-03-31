package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.RepairOrderStatus;
import com.interview.error.DuplicateResourceException;
import com.interview.error.GlobalExceptionHandler;
import com.interview.error.ResourceNotFoundException;
import com.interview.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepairOrderController.class)
@Import(GlobalExceptionHandler.class)
class RepairOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairOrderService repairOrderService;

    @Test
    void getAllReturns200() throws Exception {
        RepairOrderResponse order1 = new RepairOrderResponse(1L, "Jane Doe", "1HGCM82633A004352", "Brake pad replacement and rotor inspection", RepairOrderStatus.OPEN, new BigDecimal("325.00"), LocalDateTime.now(), LocalDateTime.now());

        RepairOrderResponse order2 = new RepairOrderResponse(2L, "John Smith", "5YJSA1E14HF123456", "Oil change and tire rotation", RepairOrderStatus.IN_PROGRESS, new BigDecimal("89.99"), LocalDateTime.now(), LocalDateTime.now());

        given(repairOrderService.getAll()).willReturn(Arrays.asList(order1, order2));

        mockMvc.perform(get("/api/repair-orders")).andExpect(status().isOk())

                // First order
                .andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].customerName").value("Jane Doe")).andExpect(jsonPath("$[0].vehicleVin").value("1HGCM82633A004352")).andExpect(jsonPath("$[0].description").value("Brake pad replacement and rotor inspection")).andExpect(jsonPath("$[0].status").value("OPEN")).andExpect(jsonPath("$[0].totalCost").value(325.00)).andExpect(jsonPath("$[0].createdAt").exists()).andExpect(jsonPath("$[0].updatedAt").exists())

                // Second order
                .andExpect(jsonPath("$[1].id").value(2)).andExpect(jsonPath("$[1].customerName").value("John Smith")).andExpect(jsonPath("$[1].vehicleVin").value("5YJSA1E14HF123456")).andExpect(jsonPath("$[1].description").value("Oil change and tire rotation")).andExpect(jsonPath("$[1].status").value("IN_PROGRESS")).andExpect(jsonPath("$[1].totalCost").value(89.99)).andExpect(jsonPath("$[1].createdAt").exists()).andExpect(jsonPath("$[1].updatedAt").exists());
    }

    @Test
    void getByIdReturns200() throws Exception {
        RepairOrderResponse response = new RepairOrderResponse(1L, "Jane Doe", "1HGCM82633A004352", "Brake pad replacement and rotor inspection", RepairOrderStatus.OPEN, new BigDecimal("325.00"), LocalDateTime.now(), LocalDateTime.now());

        given(repairOrderService.getById(1L)).willReturn(response);

        mockMvc.perform(get("/api/repair-orders/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.customerName").value("Jane Doe")).andExpect(jsonPath("$.vehicleVin").value("1HGCM82633A004352")).andExpect(jsonPath("$.description").value("Brake pad replacement and rotor inspection")).andExpect(jsonPath("$.status").value("OPEN")).andExpect(jsonPath("$.totalCost").value(325.00)).andExpect(jsonPath("$.createdAt").exists()).andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        given(repairOrderService.getById(99L)).willThrow(new ResourceNotFoundException("Repair order not found: 99"));

        mockMvc.perform(get("/api/repair-orders/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Repair order not found: 99")).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createReturns201() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("John Smith");
        request.setVehicleVin("5YJSA1E14HF123456");
        request.setDescription("Oil change and tire rotation");
        request.setStatus(RepairOrderStatus.IN_PROGRESS);
        request.setTotalCost(new BigDecimal("89.99"));

        RepairOrderResponse response = new RepairOrderResponse(3L, "John Smith", "5YJSA1E14HF123456", "Oil change and tire rotation", RepairOrderStatus.IN_PROGRESS, new BigDecimal("89.99"), LocalDateTime.now(), LocalDateTime.now());

        given(repairOrderService.create(any(RepairOrderRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/repair-orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3)).andExpect(jsonPath("$.customerName").value("John Smith")).andExpect(jsonPath("$.vehicleVin").value("5YJSA1E14HF123456")).andExpect(jsonPath("$.description").value("Oil change and tire rotation")).andExpect(jsonPath("$.status").value("IN_PROGRESS")).andExpect(jsonPath("$.totalCost").value(89.99)).andExpect(jsonPath("$.createdAt").isNotEmpty()).andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void createReturns400ForValidationFailure() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("");
        request.setVehicleVin("short");
        request.setDescription("");
        request.setStatus(null);
        request.setTotalCost(new BigDecimal("0"));

        mockMvc.perform(post("/api/repair-orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed")).andExpect(jsonPath("$.details.customerName").exists()).andExpect(jsonPath("$.details.vehicleVin").exists()).andExpect(jsonPath("$.details.description").exists()).andExpect(jsonPath("$.details.status").exists()).andExpect(jsonPath("$.details.totalCost").exists());
    }

    @Test
    void createReturns409ForDuplicateVin() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Jane Doe");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Duplicate repair order");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("100.00"));

        given(repairOrderService.create(any(RepairOrderRequest.class))).willThrow(new DuplicateResourceException("Repair order already exists for VIN: 1HGCM82633A004352"));

        mockMvc.perform(post("/api/repair-orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Repair order already exists for VIN: 1HGCM82633A004352")).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void updateReturns200() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Jane Doe");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Brake repair and rotor inspection completed");
        request.setStatus(RepairOrderStatus.COMPLETED);
        request.setTotalCost(new BigDecimal("350.00"));

        RepairOrderResponse response = new RepairOrderResponse(1L, "Jane Doe", "1HGCM82633A004352", "Brake repair and rotor inspection completed", RepairOrderStatus.COMPLETED, new BigDecimal("350.00"), LocalDateTime.now(), LocalDateTime.now());

        given(repairOrderService.update(eq(1L), any(RepairOrderRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/repair-orders/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.customerName").value("Jane Doe")).andExpect(jsonPath("$.vehicleVin").value("1HGCM82633A004352")).andExpect(jsonPath("$.description").value("Brake repair and rotor inspection completed")).andExpect(jsonPath("$.status").value("COMPLETED")).andExpect(jsonPath("$.totalCost").value(350.00)).andExpect(jsonPath("$.createdAt").isNotEmpty()).andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Missing Customer");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Missing order");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("100.00"));

        given(repairOrderService.update(eq(99L), any(RepairOrderRequest.class))).willThrow(new ResourceNotFoundException("Repair order not found: 99"));

        mockMvc.perform(put("/api/repair-orders/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Repair order not found: 99")).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateReturns400ForValidationFailure() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("");
        request.setVehicleVin("short");
        request.setDescription("");
        request.setStatus(null);
        request.setTotalCost(new BigDecimal("0"));

        mockMvc.perform(put("/api/repair-orders/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed")).andExpect(jsonPath("$.details.customerName").exists()).andExpect(jsonPath("$.details.vehicleVin").exists()).andExpect(jsonPath("$.details.description").exists()).andExpect(jsonPath("$.details.status").exists()).andExpect(jsonPath("$.details.totalCost").exists());
    }

    @Test
    void updateReturns409ForDuplicateVin() throws Exception {
        RepairOrderRequest request = new RepairOrderRequest();
        request.setCustomerName("Jane Doe");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Conflicting VIN");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("100.00"));

        given(repairOrderService.update(eq(2L), any(RepairOrderRequest.class))).willThrow(new DuplicateResourceException("Repair order already exists for VIN: 1HGCM82633A004352"));

        mockMvc.perform(put("/api/repair-orders/2").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Repair order already exists for VIN: 1HGCM82633A004352")).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deleteReturns204() throws Exception {
        willDoNothing().given(repairOrderService).delete(1L);

        mockMvc.perform(delete("/api/repair-orders/1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        org.mockito.BDDMockito.willThrow(new ResourceNotFoundException("Repair order not found: 99")).given(repairOrderService).delete(99L);

        mockMvc.perform(delete("/api/repair-orders/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Repair order not found: 99")).andExpect(jsonPath("$.status").value(404));
    }
}
