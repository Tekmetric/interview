package com.interview.service;

import com.interview.domain.RepairOrder;
import com.interview.domain.RepairOrderStatus;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;
import com.interview.dto.RepairOrderUpdateDto;
import com.interview.exception.ConflictException;
import com.interview.exception.NotFoundException;
import com.interview.repository.RepairOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @InjectMocks
    private RepairOrderService repairOrderService;

    @Test
    void create_callsRepositorySave_andMapsResponse() {
        RepairOrderCreateDto request = new RepairOrderCreateDto();
        request.setCustomerName("Customer A");
        request.setDescription("Initial");
        request.setStatus(RepairOrderStatus.OPEN);

        RepairOrder entityToSave = new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN);
        setField(entityToSave, "id", 1L);
        setField(entityToSave, "version", 0L);
        setField(entityToSave, "createdAt", Instant.now());
        setField(entityToSave, "updatedAt", Instant.now());

        when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(entityToSave);

        RepairOrderDto response = repairOrderService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVersion()).isEqualTo(0L);
        assertThat(response.getCustomerName()).isEqualTo("Customer A");
        assertThat(response.getDescription()).isEqualTo("Initial");
        assertThat(response.getStatus()).isEqualTo(RepairOrderStatus.OPEN);
    }

    @Test
    void getById_whenMissing_throwsNotFound() {
        when(repairOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairOrderService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("RepairOrder not found: 99");
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        RepairOrder entity = new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN);
        setField(entity, "id", 1L);
        setField(entity, "version", 2L);
        setField(entity, "createdAt", Instant.now());
        setField(entity, "updatedAt", Instant.now());

        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(entity));

        RepairOrderDto response = repairOrderService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVersion()).isEqualTo(2L);
        assertThat(response.getCustomerName()).isEqualTo("Customer A");
    }

    @Test
    void list_mapsPageEntitiesToPageDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        RepairOrder entity = new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN);
        setField(entity, "id", 1L);
        setField(entity, "version", 0L);
        setField(entity, "createdAt", Instant.now());
        setField(entity, "updatedAt", Instant.now());

        Page<RepairOrder> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(repairOrderRepository.findAll(pageable)).thenReturn(page);

        Page<RepairOrderDto> response = repairOrderService.list(pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void update_versionMismatch_throwsConflict() {
        RepairOrder entity = new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN);
        setField(entity, "id", 1L);
        setField(entity, "version", 1L);

        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(entity));

        RepairOrderUpdateDto request = new RepairOrderUpdateDto();
        request.setVersion(2L);
        request.setCustomerName("Customer B");
        request.setDescription("Updated");
        request.setStatus(RepairOrderStatus.COMPLETED);

        assertThatThrownBy(() -> repairOrderService.update(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Version mismatch. Client=2, Server=1");
    }

    @Test
    void update_versionMatches_updatesEntityAndSaves() {
        RepairOrder entity = new RepairOrder("Customer A", "Initial", RepairOrderStatus.OPEN);
        setField(entity, "id", 1L);
        setField(entity, "version", 1L);
        setField(entity, "createdAt", Instant.now());
        setField(entity, "updatedAt", Instant.now());

        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(repairOrderRepository.save(any(RepairOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RepairOrderUpdateDto request = new RepairOrderUpdateDto();
        request.setVersion(1L);
        request.setCustomerName("Customer B");
        request.setDescription("Updated");
        request.setStatus(RepairOrderStatus.COMPLETED);

        RepairOrderDto response = repairOrderService.update(1L, request);

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(repairOrderRepository).save(captor.capture());

        RepairOrder saved = captor.getValue();
        assertThat(saved.getCustomerName()).isEqualTo("Customer B");
        assertThat(saved.getDescription()).isEqualTo("Updated");
        assertThat(saved.getStatus()).isEqualTo(RepairOrderStatus.COMPLETED);
        assertThat(response.getCustomerName()).isEqualTo("Customer B");
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        when(repairOrderRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> repairOrderService.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("RepairOrder not found: 1");
    }

    @Test
    void delete_whenExists_deletes() {
        when(repairOrderRepository.existsById(1L)).thenReturn(true);

        repairOrderService.delete(1L);

        verify(repairOrderRepository).deleteById(1L);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
