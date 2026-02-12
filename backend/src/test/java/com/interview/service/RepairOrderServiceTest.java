package com.interview.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.interview.dto.CreateOrderDto;
import com.interview.dto.DetailOrderDto;
import com.interview.dto.ListOrderDto;
import com.interview.model.Car;
import com.interview.model.Mechanic;
import com.interview.model.RepairOrder;
import com.interview.model.RepairOrderStatus;
import com.interview.repository.CarRepository;
import com.interview.repository.MechanicRepository;
import com.interview.repository.RepairOrderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

  @Mock private RepairOrderRepository repairOrderRepository;
  @Mock private CarRepository carRepository;
  @Mock private MechanicRepository mechanicRepository;

  @InjectMocks private RepairOrderService repairOrderService;

  private Car car;
  private Mechanic mechanic;
  private RepairOrder repairOrder;

  @BeforeEach
  void setUp() {
    car = new Car();
    car.setId(1L);
    car.setLicensePlate("ABC-123");
    car.setMake("Toyota");
    car.setModel("Camry");

    mechanic = new Mechanic();
    mechanic.setId(1L);
    mechanic.setName("John");
    mechanic.setSpecialization("Engine");

    repairOrder = new RepairOrder();
    repairOrder.setId(1L);
    repairOrder.setOrderDate(LocalDate.of(2024, 1, 15));
    repairOrder.setStatus(RepairOrderStatus.PLACED);
    repairOrder.setCar(car);
    repairOrder.setMechanics(Set.of(mechanic));
  }

  @Test
  void getAllOrders_withPagination_returnsPageOfOrders() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<RepairOrder> page = new PageImpl<>(List.of(repairOrder));
    when(repairOrderRepository.findAllWithDetails(pageable)).thenReturn(page);

    Page<ListOrderDto> result = repairOrderService.getAllOrders(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(repairOrderRepository).findAllWithDetails(pageable);
  }

  @Test
  void getOrderById_existingId_returnsOrder() {
    when(repairOrderRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(repairOrder));

    Optional<DetailOrderDto> result = repairOrderService.getOrderById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    verify(repairOrderRepository).findByIdWithDetails(1L);
  }

  @Test
  void getOrderById_nonExistingId_returnsEmpty() {
    when(repairOrderRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

    Optional<DetailOrderDto> result = repairOrderService.getOrderById(999L);

    assertFalse(result.isPresent());
    verify(repairOrderRepository).findByIdWithDetails(999L);
  }

  @Test
  void createOrder_withNewCar_createsCarAndOrder() {
    CreateOrderDto createDto = new CreateOrderDto();
    createDto.setOrderDate(LocalDate.of(2024, 1, 15));
    createDto.setStatus(RepairOrderStatus.PLACED);

    CreateOrderDto.CarDto carDto = new CreateOrderDto.CarDto();
    carDto.setId(0L);
    carDto.setLicensePlate("NEW-123");
    carDto.setMake("Honda");
    carDto.setModel("Civic");
    createDto.setCar(carDto);
    createDto.setMechanicIds(Set.of(1L));

    when(carRepository.save(any(Car.class))).thenReturn(car);
    when(mechanicRepository.findAllById(Set.of(1L))).thenReturn(List.of(mechanic));
    when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(repairOrder);

    DetailOrderDto result = repairOrderService.createOrder(createDto);

    assertNotNull(result);
    verify(carRepository).save(any(Car.class));
    verify(mechanicRepository).findAllById(Set.of(1L));
    verify(repairOrderRepository).save(any(RepairOrder.class));
  }

  @Test
  void createOrder_withExistingCar_usesExistingCar() {
    CreateOrderDto createDto = new CreateOrderDto();
    createDto.setOrderDate(LocalDate.of(2024, 1, 15));
    createDto.setStatus(RepairOrderStatus.PLACED);

    CreateOrderDto.CarDto carDto = new CreateOrderDto.CarDto();
    carDto.setId(1L);
    createDto.setCar(carDto);

    when(carRepository.findById(1L)).thenReturn(Optional.of(car));
    when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(repairOrder);

    DetailOrderDto result = repairOrderService.createOrder(createDto);

    assertNotNull(result);
    verify(carRepository).findById(1L);
    verify(carRepository, never()).save(any(Car.class));
    verify(repairOrderRepository).save(any(RepairOrder.class));
  }

  @Test
  void deleteOrder_callsRepository() {
    repairOrderService.deleteOrder(1L);

    verify(repairOrderRepository).deleteById(1L);
  }
}
