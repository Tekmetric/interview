package com.interview.workorder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.common.error.ApiExceptionHandler;
import com.interview.common.error.ResourceNotFoundException;
import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;
import com.interview.workorder.service.WorkOrderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class WorkOrderControllerUnitTest {

    @Mock
    private WorkOrderService workOrderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        WorkOrderController controller = new WorkOrderController(workOrderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createShouldReturnCreatedWorkOrder() throws Exception {
        WorkOrderResponse response = response(100L, 1L, "1HGCM82633A004352", "OPEN");
        when(workOrderService.create(eq(1L), any(WorkOrderRequest.class))).thenReturn(response);

        Map<String, Object> payload = Map.of(
                "vin", "1HGCM82633A004352",
                "issueDescription", "Brake pads replacement",
                "status", "OPEN"
        );

        mockMvc.perform(post("/api/customers/1/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.vin").value("1HGCM82633A004352"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        verify(workOrderService).create(eq(1L), any(WorkOrderRequest.class));
    }

    @Test
    void createShouldReturnBadRequestWhenPayloadInvalid() throws Exception {
        Map<String, Object> invalidPayload = Map.of(
                "vin", "",
                "issueDescription", "",
                "status", "UNKNOWN"
        );

        mockMvc.perform(post("/api/customers/1/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.vin").value("vin must be 11-17 uppercase alphanumeric characters"));

        verify(workOrderService, never()).create(eq(1L), any(WorkOrderRequest.class));
    }

    @Test
    void listShouldReturnCustomerScopedItems() throws Exception {
        when(workOrderService.list(1L)).thenReturn(List.of(
                response(1L, 1L, "1HGCM82633A004352", "OPEN"),
                response(2L, 1L, "JH4KA9650MC012345", "IN_PROGRESS")
        ));

        mockMvc.perform(get("/api/customers/1/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));

        verify(workOrderService).list(1L);
    }

    @Test
    void getByIdShouldReturnNotFoundWhenServiceThrows() throws Exception {
        when(workOrderService.getById(1L, 99L))
                .thenThrow(new ResourceNotFoundException(com.interview.workorder.entity.WorkOrder.class, 99L));

        mockMvc.perform(get("/api/customers/1/work-orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("WorkOrder with id 99 was not found"));
    }

    @Test
    void updateAndDeleteShouldDelegateToService() throws Exception {
        WorkOrderResponse updated = response(5L, 1L, "1HGCM82633A004352", "COMPLETED");
        when(workOrderService.update(eq(1L), eq(5L), any(WorkOrderRequest.class))).thenReturn(updated);

        Map<String, Object> updatePayload = Map.of(
                "vin", "1HGCM82633A004352",
                "issueDescription", "Issue resolved",
                "status", "COMPLETED"
        );

        mockMvc.perform(put("/api/customers/1/work-orders/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(delete("/api/customers/1/work-orders/5"))
                .andExpect(status().isNoContent());

        verify(workOrderService).update(eq(1L), eq(5L), any(WorkOrderRequest.class));
        verify(workOrderService).delete(1L, 5L);
    }

    private static WorkOrderResponse response(Long id, Long customerId, String vin, String status) {
        return new WorkOrderResponse(
                id,
                customerId,
                vin,
                "Issue",
                status,
                LocalDateTime.of(2026, 3, 4, 10, 0),
                LocalDateTime.of(2026, 3, 4, 10, 30)
        );
    }
}
