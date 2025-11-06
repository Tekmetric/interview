package com.interview.repair_order.service;

import com.interview._infrastructure.exceptions.BadRequestException;
import com.interview._infrastructure.exceptions.NotFoundException;
import com.interview.repair_order.api.model.RepairOrderRequest;
import com.interview.repair_order.api.model.RepairOrderResponse;
import com.interview.repair_order.domain.RepairOrder;
import com.interview.repair_order.domain.Status;
import com.interview.repair_order.repository.RepairOrderRepository;
import com.interview.repair_order_line.domain.RepairOrderLine;
import com.interview.repair_order_line.repository.RepairOrderLineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    private static final String REPAIR_ORDER_ID = "100682ed-01b7-42c4-961d-08d158494d5c";
    private static final String NOT_FOUND = "A repair order with ID: %s cannot be found.";
    private static final String SHOP_ID = "f0fc529a-4ec1-4616-bea2-4a1b014bb473";
    private static final String EXTERNAL_RO = "externalRO";
    private static final Status STATUS = Status.APPROVED;
    private static final String NOTES = "Notes";

    @Mock
    private RepairOrderRepository repairOrderRepository;
    @Mock
    private RepairOrderLineRepository repairOrderLineRepository;
    @Mock
    private Pageable pageable;
    @Mock
    private RepairOrder repairOrder;
    @Mock
    private RepairOrderRequest repairOrderRequest;
    @Mock
    private List<RepairOrderLine> repairOrderLines;

    @InjectMocks
    private RepairOrderService repairOrderService;

    @Test
    void getAllPaginated() {
        RepairOrder ro1 = new RepairOrder();
        RepairOrder ro2 = new RepairOrder();
        Page<RepairOrder> repairOrderPage = new PageImpl<>(
                List.of(ro1, ro2),
                PageRequest.of(0, 10),
                2
        );
        when(repairOrderRepository.findAllWithLinesPageable(pageable)).thenReturn(repairOrderPage);

        Page<RepairOrderResponse> result = repairOrderService.getAllPaginated(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void getRepairOrder() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.of(repairOrder));
        when(repairOrder.getId()).thenReturn(REPAIR_ORDER_ID);

        RepairOrderResponse response = repairOrderService.getRepairOrder(REPAIR_ORDER_ID);
        assertEquals(REPAIR_ORDER_ID, response.getId());
    }

    @Test
    void getRepairOrder_notFound() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> repairOrderService.getRepairOrder(REPAIR_ORDER_ID));

        assertEquals(String.format(NOT_FOUND, REPAIR_ORDER_ID), exception.getMessage());
    }

    @Test
    void createRepairOrder() {
        when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(repairOrder);

        RepairOrderResponse response = repairOrderService.createRepairOrder(repairOrderRequest);

        assertNotNull(response);
    }

    @Test
    void updateRepairOrder() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.of(repairOrder));
        when(repairOrderRequest.getShopId()).thenReturn(SHOP_ID);
        when(repairOrderRequest.getExternalRO()).thenReturn(EXTERNAL_RO);
        when(repairOrderRequest.getStatus()).thenReturn(STATUS);
        when(repairOrderRequest.getNotes()).thenReturn(NOTES);
        when(repairOrderRequest.getOdometerIn()).thenReturn(null);
        when(repairOrderRequest.getOdometerOut()).thenReturn(null);
        when(repairOrder.getId()).thenReturn(REPAIR_ORDER_ID);
        when(repairOrder.getShopId()).thenReturn(SHOP_ID);
        when(repairOrder.getExternalRO()).thenReturn(EXTERNAL_RO);
        when(repairOrder.getStatus()).thenReturn(STATUS);
        when(repairOrder.getNotes()).thenReturn(NOTES);
        when(repairOrder.getOdometerIn()).thenReturn(null);
        when(repairOrder.getOdometerOut()).thenReturn(null);

        RepairOrderResponse response = repairOrderService.updateRepairOrder(REPAIR_ORDER_ID, repairOrderRequest);

        assertNotNull(response);
        verify(repairOrder).setShopId(SHOP_ID);
        verify(repairOrder).setExternalRO(EXTERNAL_RO);
        verify(repairOrder).setStatus(STATUS);
        verify(repairOrder).setNotes(NOTES);
        verify(repairOrder).setOdometerIn(null);
        verify(repairOrder).setOdometerOut(null);
        assertEquals(REPAIR_ORDER_ID, response.getId());
        assertEquals(SHOP_ID, response.getShopId());
        assertEquals(EXTERNAL_RO, response.getExternalRO());
        assertEquals(STATUS, response.getStatus());
        assertEquals(NOTES, response.getNotes());
        assertNull(response.getOdometerIn());
        assertNull(response.getOdometerOut());
    }

    @Test
    void updateRepairOrder_notFound() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> repairOrderService.updateRepairOrder(REPAIR_ORDER_ID, repairOrderRequest));

        assertEquals(String.format(NOT_FOUND, REPAIR_ORDER_ID), exception.getMessage());
    }

    @Test
    void updateRepairOrder_odometerOutLessThanOdometerIn() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.of(repairOrder));
        when(repairOrderRequest.getOdometerIn()).thenReturn(100);
        when(repairOrderRequest.getOdometerOut()).thenReturn(10);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> repairOrderService.updateRepairOrder(REPAIR_ORDER_ID, repairOrderRequest));

        assertEquals("Odometer In cannot be greater than Odometer Out.", exception.getMessage());
    }

    @Test
    void deleteRepairOrder() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.of(repairOrder));
        when(repairOrder.getRepairOrderLines()).thenReturn(repairOrderLines);

        assertDoesNotThrow(() -> repairOrderService.deleteRepairOrder(REPAIR_ORDER_ID));
        verify(repairOrderLineRepository).deleteAll(repairOrderLines);
        verify(repairOrderLines).clear();
        verify(repairOrderRepository).delete(repairOrder);
    }

    @Test
    void deleteRepairOrder_notFound() {
        when(repairOrderRepository.findById(REPAIR_ORDER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> repairOrderService.deleteRepairOrder(REPAIR_ORDER_ID));

        verifyNoInteractions(repairOrderLineRepository);
        verify(repairOrderRepository, never()).delete(any());
    }
}
