package com.interview.repair_order.api.controller;

import com.interview.repair_order.api.model.RepairOrderRequest;
import com.interview.repair_order.api.model.RepairOrderResponse;
import com.interview.repair_order.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairOrderControllerTest {

    private static final int PAGE_COUNT = 0;
    private static final int PAGE_SIZE = 20;
    private static final String REPAIR_ORDER_ID = "100682ed-01b7-42c4-961d-08d158494d5c";

    @Mock
    private RepairOrderService repairOrderService;
    @Mock
    private Page<RepairOrderResponse> repairOrderResponses;
    @Mock
    private RepairOrderRequest repairOrderRequest;
    @Mock
    private RepairOrderResponse repairOrderResponse;
    @Mock
    private HttpServletRequest httpServletRequest;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @InjectMocks
    private RepairOrderController repairOrderController;

    @Test
    void create() {
        //set this up so we can derive the location path
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
        when(repairOrderService.createRepairOrder(repairOrderRequest)).thenReturn(repairOrderResponse);
        when(repairOrderResponse.getId()).thenReturn(REPAIR_ORDER_ID);

        ResponseEntity<RepairOrderResponse> response = repairOrderController.create(repairOrderRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(repairOrderResponse, response.getBody());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll() {
        when(repairOrderService.getAllPaginated(any(Pageable.class))).thenReturn(repairOrderResponses);

        Page<RepairOrderResponse> result = repairOrderController.getAll(PAGE_COUNT);

        verify(repairOrderService).getAllPaginated(pageableCaptor.capture());
        assertEquals(repairOrderResponses, result);
        Pageable actualPageable = pageableCaptor.getValue();
        assertEquals(PAGE_COUNT, actualPageable.getOffset());
        assertEquals(PAGE_SIZE, actualPageable.getPageSize());
    }

    @Test
    void getRepairOrder() {
        when(repairOrderService.getRepairOrder(REPAIR_ORDER_ID)).thenReturn(repairOrderResponse);

        RepairOrderResponse response = repairOrderController.getRepairOrder(REPAIR_ORDER_ID);

        assertEquals(repairOrderResponse, response);
    }

    @Test
    void updateRepairOrder() {
        when(repairOrderService.updateRepairOrder(REPAIR_ORDER_ID, repairOrderRequest)).thenReturn(repairOrderResponse);

        RepairOrderResponse response = repairOrderController.updateRepairOrder(REPAIR_ORDER_ID, repairOrderRequest);

        assertEquals(repairOrderResponse, response);
    }

    @Test
    void deleteRepairOrder() {
        ResponseEntity<Void> response = repairOrderController.deleteRepairOrder(REPAIR_ORDER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(repairOrderService).deleteRepairOrder(REPAIR_ORDER_ID);
    }
}
