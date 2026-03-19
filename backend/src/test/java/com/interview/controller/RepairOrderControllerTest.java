package com.interview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CreateOrderDto;
import com.interview.dto.DetailOrderDto;
import com.interview.dto.ListOrderDto;
import com.interview.model.RepairOrderStatus;
import com.interview.service.RepairOrderService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RepairOrderController.class)
class RepairOrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private RepairOrderService repairOrderService;

  @Test
  void getAllOrders_returnsPageOfOrders() throws Exception {
    ListOrderDto orderDto = new ListOrderDto();
    orderDto.setId(1L);
    orderDto.setOrderDate(LocalDate.of(2024, 1, 15));
    orderDto.setStatus(RepairOrderStatus.PLACED);

    Page<ListOrderDto> page = new PageImpl<>(List.of(orderDto));
    when(repairOrderService.getAllOrders(0, 10)).thenReturn(page);

    mockMvc
        .perform(get("/api/v1/orders?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].status").value("PLACED"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void getOrderById_existingId_returnsOrder() throws Exception {
    DetailOrderDto orderDto = new DetailOrderDto();
    orderDto.setId(1L);
    orderDto.setOrderDate(LocalDate.of(2024, 1, 15));
    orderDto.setStatus(RepairOrderStatus.PLACED);

    when(repairOrderService.getOrderById(1L)).thenReturn(Optional.of(orderDto));

    mockMvc
        .perform(get("/api/v1/orders/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("PLACED"));
  }

  @Test
  void getOrderById_nonExistingId_returnsNotFound() throws Exception {
    when(repairOrderService.getOrderById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/orders/999")).andExpect(status().isNotFound());
  }

  @Test
  void createOrder_validRequest_returnsCreated() throws Exception {
    CreateOrderDto createDto = new CreateOrderDto();
    createDto.setOrderDate(LocalDate.of(2024, 1, 15));
    createDto.setStatus(RepairOrderStatus.PLACED);

    CreateOrderDto.CarDto carDto = new CreateOrderDto.CarDto();
    carDto.setId(0L);
    carDto.setLicensePlate("ABC-123");
    carDto.setMake("Toyota");
    carDto.setModel("Camry");
    createDto.setCar(carDto);
    createDto.setMechanicIds(Set.of(1L));

    DetailOrderDto responseDto = new DetailOrderDto();
    responseDto.setId(1L);
    responseDto.setOrderDate(LocalDate.of(2024, 1, 15));
    responseDto.setStatus(RepairOrderStatus.PLACED);

    when(repairOrderService.createOrder(any(CreateOrderDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("PLACED"));
  }

  @Test
  void updateOrder_validRequest_returnsOk() throws Exception {
    CreateOrderDto updateDto = new CreateOrderDto();
    updateDto.setOrderDate(LocalDate.of(2024, 1, 20));
    updateDto.setStatus(RepairOrderStatus.IN_PROGRESS);

    CreateOrderDto.CarDto carDto = new CreateOrderDto.CarDto();
    carDto.setId(1L);
    updateDto.setCar(carDto);

    DetailOrderDto responseDto = new DetailOrderDto();
    responseDto.setId(1L);
    responseDto.setOrderDate(LocalDate.of(2024, 1, 20));
    responseDto.setStatus(RepairOrderStatus.IN_PROGRESS);

    when(repairOrderService.updateOrder(eq(1L), any(CreateOrderDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            put("/api/v1/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
  }

  @Test
  void deleteOrder_validId_returnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/orders/1")).andExpect(status().isNoContent());
  }
}
