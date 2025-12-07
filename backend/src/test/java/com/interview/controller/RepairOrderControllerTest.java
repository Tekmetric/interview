package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import com.interview.model.RepairOrderStatus;
import com.interview.model.exception.EntityNotFoundException;
import com.interview.service.RepairOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepairOrderController.class)
class RepairOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RepairOrderService repairOrderService;

    private RepairOrderDto repairOrderDto;

    @BeforeEach
    void setup() {
        repairOrderDto = RepairOrderDto.builder()
                .id(1L)
                .vin("WAUZZZ8V3JA123456")
                .carModel("Audi A3")
                .issueDescription("Car doesn't start")
                .status(RepairOrderStatus.DRAFT)
                .build();
    }

    @Test
    void givenValidCreateRepairOrderRequest_whenCreateRepairOrder_thenSuccess() throws Exception {
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(
                "WAUZZZ8V3JA123456",
                "Audi A3",
                "Car doesn't start"
        );

        Mockito.when(repairOrderService.create(any(CreateRepairOrderRequest.class))).thenReturn(repairOrderDto);

        mockMvc.perform(post("/api/v1/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("WAUZZZ8V3JA123456"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void givenCreateRepairOrderRequestWithEmptyVin_whenCreateRepairOrder_thenValidationErrorIsThrow() throws Exception {
        CreateRepairOrderRequest request = new CreateRepairOrderRequest("", "Audi a3", "Car doesn't start");

        mockMvc.perform(post("/api/v1/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-fields").isArray())
                .andExpect(jsonPath("$.invalid-fields[0].name").value("vin"));
    }

    @Test
    void givenRequestWithVinTooLong_whenCreateRepairOrder_thenValidationErrorIsThrow() throws Exception {
        String longVin = "a".repeat(300);
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(
                longVin,
                "Audi 13",
                "Doesn't start"
        );

        mockMvc.perform(post("/api/v1/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-fields[?(@.name=='vin')]").exists());
    }

    @Test
    void givenExistingOrderId_whenGetRepairOrderById_thenSuccess() throws Exception {
        Mockito.when(repairOrderService.findById(anyLong())).thenReturn(repairOrderDto);

        mockMvc.perform(get("/api/v1/repair-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("WAUZZZ8V3JA123456"));
    }

    @Test
    void givenNonExistingId_whenGetRepairOrderById_shouldReturn404() throws Exception {
        Mockito.when(repairOrderService.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Repair order not found"));

        mockMvc.perform(get("/api/v1/repair-orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenRepairOrders_whenGetAllRepairOrders_thenSuccess() throws Exception {
        Page<RepairOrderDto> page = new PageImpl<>(List.of(repairOrderDto));

        Mockito.when(repairOrderService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/repair-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].vin").value("WAUZZZ8V3JA123456"));
    }

    @Test
    void givenValidUpdateRepairOrderRequest_whenUpdateRepairOrder_thenSuccess() throws Exception {
        UpdateRepairOrderRequest request = new UpdateRepairOrderRequest("Updated issue", RepairOrderStatus.IN_PROGRESS);
        RepairOrderDto updatedDto = RepairOrderDto.builder()
                .id(1L)
                .vin("WAUZZZ8V3JA123456")
                .carModel("Audi A3")
                .issueDescription("Updated issue")
                .status(RepairOrderStatus.IN_PROGRESS)
                .build();

        Mockito.when(repairOrderService.update(anyLong(), any(UpdateRepairOrderRequest.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/repair-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issueDescription").value("Updated issue"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void givenIssueDescriptionTooLong_whenUpdateRepairOrder_thenValidationErrorIsThrow() throws Exception {
        String longIssueDescription = "a".repeat(300);
        UpdateRepairOrderRequest request = new UpdateRepairOrderRequest(
                longIssueDescription,
                com.interview.model.RepairOrderStatus.DRAFT
        );

        mockMvc.perform(put("/api/v1/repair-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-fields[?(@.name=='issueDescription')]").exists());
    }

    @Test
    void givenNullStatus_whenUpdateRepairOrder_thenValidationErrorIsThrow() throws Exception {
        String json = """
            {
                "issueDescription": "Updated issue",
                "status": null
            }
            """;

        mockMvc.perform(put("/api/v1/repair-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-fields[?(@.name=='status')]").exists());
    }

    @Test
    void givenExistingId_whenDeleteRepairOrder_ThenSuccess() throws Exception {
        Mockito.doNothing().when(repairOrderService).deleteById(anyLong());

        mockMvc.perform(delete("/api/v1/repair-orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNonExistingId_whenDeleteRepairOrder_thenNotFound() throws Exception {
        Mockito.doThrow(new com.interview.model.exception.EntityNotFoundException("Repair order not found"))
                .when(repairOrderService).deleteById(anyLong());

        mockMvc.perform(delete("/api/v1/repair-orders/999"))
                .andExpect(status().isNotFound());
    }

}