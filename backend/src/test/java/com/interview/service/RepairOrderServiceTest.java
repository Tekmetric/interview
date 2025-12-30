package com.interview.service;

import com.interview.dto.RepairLineItemRequest;
import com.interview.dto.RepairOrderCreateRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.RepairOrder;
import com.interview.exception.ConflictException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock RepairOrderRepository repo;
    @InjectMocks RepairOrderService service;

    @Test
    public void createComputesTotalFromLineItems() {
        when(repo.existsByOrderNumber("RO-3000")).thenReturn(false);
        // We capture the RepairOrder passed in so we can mock the ID and version ETag being set by JPA
        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        when(repo.save(captor.capture())).thenAnswer(inv -> {
            RepairOrder order = captor.getValue();
            order.setId(123L);
            order.setVersion(0L);
            return order;
        });

        var req = new RepairOrderCreateRequest("RO-3000",
                "2C4RC1BG4HR888888",
                2021,
                "Ford",
                "F-150",
                "Alex",
                "+1-555-0300",
                List.of(new RepairLineItemRequest("Oil", 1, new BigDecimal("50.00")),
                        new RepairLineItemRequest("Filter", 2, new BigDecimal("10.00")))
        );

        RepairOrderResponse resp = service.create(req);
        assertEquals(new BigDecimal("70.00"), resp.total());
        assertEquals(0L, resp.version());
        assertNotNull(resp.id());
    }

    @Test
    public void verifyVersionHeaderThrowsOnMismatch() {
        RepairOrder order = new RepairOrder();
        order.setId(1L);
        order.setVersion(5L);
        when(repo.findById(1L)).thenReturn(Optional.of(order));

        ConflictException ex = assertThrows(ConflictException.class, () -> service.verifyVersionHeader(1L, "\"4\""));
        assertTrue(ex.getMessage().contains("ETag mismatch"));
    }

    @Test
    public void verifyVersionHeaderThrowsNotFound() {
        when(repo.findById(42L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.verifyVersionHeader(42L, "\"1\""));
    }
}
