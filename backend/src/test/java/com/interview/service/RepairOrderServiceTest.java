package com.interview.service;

import com.interview.dto.RepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import com.interview.error.DuplicateResourceException;
import com.interview.error.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repository;

    @InjectMocks
    private RepairOrderService service;

    private RepairOrder repairOrder;
    private RepairOrderRequest request;

    @BeforeEach
    void setUp() {
        repairOrder = new RepairOrder();
        repairOrder.setId(1L);
        repairOrder.setCustomerName("Jane Doe");
        repairOrder.setVehicleVin("1HGCM82633A004352");
        repairOrder.setDescription("Brake pad replacement");
        repairOrder.setStatus(RepairOrderStatus.OPEN);
        repairOrder.setTotalCost(new BigDecimal("325.00"));

        request = new RepairOrderRequest();
        request.setCustomerName("Jane Doe");
        request.setVehicleVin("1HGCM82633A004352");
        request.setDescription("Brake pad replacement");
        request.setStatus(RepairOrderStatus.OPEN);
        request.setTotalCost(new BigDecimal("325.00"));
    }

    @Test
    void getAllReturnsMappedResponses() {
        when(repository.findAll()).thenReturn(Collections.singletonList(repairOrder));

        List<RepairOrderResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCustomerName()).isEqualTo("Jane Doe");
        assertThat(result.get(0).getVehicleVin()).isEqualTo("1HGCM82633A004352");
    }

    @Test
    void getByIdReturnsResponseWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(repairOrder));

        RepairOrderResponse result = service.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("Jane Doe");
        assertThat(result.getStatus()).isEqualTo(RepairOrderStatus.OPEN);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Repair order not found with id: 99");
    }

    @Test
    void createThrowsWhenDuplicateVinExists() {
        when(repository.existsByVehicleVin("1HGCM82633A004352")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Repair order already exists for VIN: 1HGCM82633A004352");

        verify(repository, never()).save(any(RepairOrder.class));
    }

    @Test
    void createSavesAndReturnsResponse() {
        when(repository.existsByVehicleVin("1HGCM82633A004352")).thenReturn(false);
        when(repository.save(any(RepairOrder.class))).thenAnswer(invocation -> {
            RepairOrder saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        RepairOrderResponse result = service.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("Jane Doe");
        assertThat(result.getVehicleVin()).isEqualTo("1HGCM82633A004352");
        verify(repository).save(any(RepairOrder.class));
    }

    @Test
    void updateThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Repair order not found with id: 99");
    }

    @Test
    void updateThrowsWhenDuplicateVinExistsOnAnotherRecord() {
        when(repository.findById(1L)).thenReturn(Optional.of(repairOrder));
        when(repository.existsByVehicleVinAndIdNot("1HGCM82633A004352", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Repair order already exists for VIN: 1HGCM82633A004352");

        verify(repository, never()).save(any(RepairOrder.class));
    }

    @Test
    void updateSavesAndReturnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(repairOrder));
        when(repository.existsByVehicleVinAndIdNot("1HGCM82633A004352", 1L)).thenReturn(false);
        when(repository.save(any(RepairOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        request.setDescription("Brake repair completed");
        request.setStatus(RepairOrderStatus.COMPLETED);
        request.setTotalCost(new BigDecimal("350.00"));

        RepairOrderResponse result = service.update(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Brake repair completed");
        assertThat(result.getStatus()).isEqualTo(RepairOrderStatus.COMPLETED);
        assertThat(result.getTotalCost()).isEqualByComparingTo("350.00");
    }

    @Test
    void deleteDeletesWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(repairOrder));

        service.delete(1L);

        verify(repository).delete(repairOrder);
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Repair order not found with id: 99");

        verify(repository, never()).delete(any(RepairOrder.class));
    }
}
