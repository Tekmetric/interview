package com.interview.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.interview.model.Car;
import com.interview.model.Mechanic;
import com.interview.model.RepairOrder;
import com.interview.model.RepairOrderStatus;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderDtoMapperTest {

  @Test
  void convertToDetailDto_withFullOrder_mapsAllFields() {
    Car car = new Car();
    car.setId(1L);
    car.setLicensePlate("ABC-123");
    car.setMake("Toyota");
    car.setModel("Camry");

    Mechanic mechanic = new Mechanic();
    mechanic.setId(1L);
    mechanic.setName("John");
    mechanic.setSpecialization("Engine");

    RepairOrder order = new RepairOrder();
    order.setId(1L);
    order.setOrderDate(LocalDate.of(2024, 1, 15));
    order.setStatus(RepairOrderStatus.COMPLETED);
    order.setCar(car);
    order.setMechanics(Set.of(mechanic));

    DetailOrderDto result = OrderDtoMapper.convertToDetailDto(order);

    assertEquals(1L, result.getId());
    assertEquals(LocalDate.of(2024, 1, 15), result.getOrderDate());
    assertEquals(RepairOrderStatus.COMPLETED, result.getStatus());
    assertEquals("ABC-123", result.getCar().getLicensePlate());
    assertEquals(1, result.getMechanics().size());
  }

  @Test
  void convertToDetailDto_withNullCar_handlesGracefully() {
    RepairOrder order = new RepairOrder();
    order.setId(1L);
    order.setOrderDate(LocalDate.of(2024, 1, 15));
    order.setStatus(RepairOrderStatus.PLACED);

    DetailOrderDto result = OrderDtoMapper.convertToDetailDto(order);

    assertNull(result.getCar());
  }

  @Test
  void convertToListDto_withFullOrder_mapsAllFields() {
    Car car = new Car();
    car.setLicensePlate("XYZ-789");
    car.setMake("Honda");
    car.setModel("Civic");

    Mechanic mechanic = new Mechanic();
    mechanic.setName("Jane");

    RepairOrder order = new RepairOrder();
    order.setId(2L);
    order.setOrderDate(LocalDate.of(2024, 2, 20));
    order.setStatus(RepairOrderStatus.IN_PROGRESS);
    order.setCar(car);
    order.setMechanics(Set.of(mechanic));

    ListOrderDto result = OrderDtoMapper.convertToListDto(order);

    assertEquals(2L, result.getId());
    assertEquals("XYZ-789", result.getCar().getLicensePlate());
    assertEquals(1, result.getMechanics().size());
  }

  @Test
  void convertToListDto_withEmptyMechanics_handlesGracefully() {
    RepairOrder order = new RepairOrder();
    order.setId(1L);
    order.setOrderDate(LocalDate.of(2024, 1, 15));
    order.setStatus(RepairOrderStatus.PLACED);
    order.setMechanics(new HashSet<>());

    ListOrderDto result = OrderDtoMapper.convertToListDto(order);

    assertNotNull(result.getMechanics());
    assertTrue(result.getMechanics().isEmpty());
  }
}
