package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.ServiceOrderMapper;
import com.interview.model.dto.ServiceOrderDTO;
import com.interview.model.entity.ServiceOrder;
import com.interview.model.entity.Vehicle;
import com.interview.model.enums.ServiceOrderStatus;
import com.interview.repository.ServiceOrderRepository;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private Vehicle vehicle;
    private ServiceOrder serviceOrder;
    private ServiceOrderDTO serviceOrderDto;
    private final String VIN = "VIN1234567890ABCD";

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setVin(VIN);

        serviceOrder = new ServiceOrder();
        serviceOrder.setId(100L);
        serviceOrder.setDescription("Oil Change");
        serviceOrder.setStatus(ServiceOrderStatus.PENDING);

        serviceOrderDto = new ServiceOrderDTO("Oil Change", LocalDateTime.now(), ServiceOrderStatus.PENDING);
    }

    @Test
    void addServiceOrder_ShouldLinkToVehicleAndSave() {
        when(vehicleRepository.findByVin(VIN)).thenReturn(Optional.of(vehicle));
        when(serviceOrderMapper.toEntity(serviceOrderDto)).thenReturn(serviceOrder);
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenReturn(serviceOrder);
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDto);

        ServiceOrderDTO result = serviceOrderService.addServiceOrder(VIN, serviceOrderDto);

        assertThat(result).isNotNull();
        assertThat(serviceOrder.getVehicles()).contains(vehicle);
        assertThat(vehicle.getServiceOrders()).contains(serviceOrder);
        verify(serviceOrderRepository).save(serviceOrder);
    }

    @Test
    void updateServiceOrder_ShouldUpdateFieldsAndSave() {
        Long orderId = 100L;
        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderRepository.save(serviceOrder)).thenReturn(serviceOrder);
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDto);

        serviceOrderService.updateServiceOrder(orderId, serviceOrderDto);

        verify(serviceOrderMapper).updateEntityFromDto(serviceOrderDto, serviceOrder);
        verify(serviceOrderRepository).save(serviceOrder);
    }

    @Test
    void removeServiceOrderFromVehicle_ShouldUnlinkAndCleanupOrphan() {
        serviceOrder.addVehicle(vehicle);
        when(vehicleRepository.findByVin(VIN)).thenReturn(Optional.of(vehicle));
        when(serviceOrderRepository.findById(100L)).thenReturn(Optional.of(serviceOrder));

        serviceOrderService.removeServiceOrderFromVehicle(VIN, 100L);

        assertThat(serviceOrder.getVehicles()).doesNotContain(vehicle);
        assertThat(vehicle.getServiceOrders()).doesNotContain(serviceOrder);

        verify(serviceOrderRepository).delete(serviceOrder);
    }

    @Test
    void removeServiceOrderFromVehicle_ShouldNotDeleteIfOtherVehiclesExist() {
        Vehicle anotherVehicle = new Vehicle();
        anotherVehicle.setId(2L);

        serviceOrder.addVehicle(vehicle);
        serviceOrder.addVehicle(anotherVehicle);

        when(vehicleRepository.findByVin(VIN)).thenReturn(Optional.of(vehicle));
        when(serviceOrderRepository.findById(100L)).thenReturn(Optional.of(serviceOrder));

        serviceOrderService.removeServiceOrderFromVehicle(VIN, 100L);

        assertThat(serviceOrder.getVehicles()).contains(anotherVehicle);
        assertThat(serviceOrder.getVehicles()).doesNotContain(vehicle);

        verify(serviceOrderRepository).save(serviceOrder);
        verify(serviceOrderRepository, never()).delete(any());
    }

    @Test
    void addServiceOrder_ShouldThrowException_WhenVehicleNotFound() {
        when(vehicleRepository.findByVin("INVALID")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serviceOrderService.addServiceOrder("INVALID", serviceOrderDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}