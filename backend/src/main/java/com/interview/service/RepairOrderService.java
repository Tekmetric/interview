package com.interview.service;

import static com.interview.dto.OrderDtoMapper.convertToDetailDto;

import com.interview.dto.CreateOrderDto;
import com.interview.dto.DetailOrderDto;
import com.interview.dto.ListOrderDto;
import com.interview.dto.OrderDtoMapper;
import com.interview.model.Car;
import com.interview.model.RepairOrder;
import com.interview.repository.CarRepository;
import com.interview.repository.MechanicRepository;
import com.interview.repository.RepairOrderRepository;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public class RepairOrderService {

  private final RepairOrderRepository repairOrderRepository;
  private final CarRepository carRepository;
  private final MechanicRepository mechanicRepository;

  public RepairOrderService(
      RepairOrderRepository repairOrderRepository,
      CarRepository carRepository,
      MechanicRepository mechanicRepository) {
    this.repairOrderRepository = repairOrderRepository;
    this.carRepository = carRepository;
    this.mechanicRepository = mechanicRepository;
  }

  public Page<ListOrderDto> getAllOrders(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repairOrderRepository.findAllWithDetails(pageable).map(OrderDtoMapper::convertToListDto);
  }

  public Optional<DetailOrderDto> getOrderById(Long id) {
    return repairOrderRepository.findByIdWithDetails(id).map(OrderDtoMapper::convertToDetailDto);
  }

  @Transactional
  public DetailOrderDto createOrder(CreateOrderDto createDto) {
    Car car = findOrCreateCar(createDto.getCar());

    RepairOrder repairOrder = new RepairOrder();
    repairOrder.setOrderDate(createDto.getOrderDate());
    repairOrder.setStatus(createDto.getStatus());
    repairOrder.setCar(car);

    if (createDto.getMechanicIds() != null && !createDto.getMechanicIds().isEmpty()) {
      repairOrder.setMechanics(
          new HashSet<>(mechanicRepository.findAllById(createDto.getMechanicIds())));
    }

    repairOrder = repairOrderRepository.save(repairOrder);
    return convertToDetailDto(repairOrder);
  }

  @Transactional
  public DetailOrderDto updateOrder(Long id, CreateOrderDto updateDto) {
    Car car = findOrCreateCar(updateDto.getCar());

    RepairOrder repairOrder =
        repairOrderRepository
            .findById(id)
            .map(
                order -> {
                  order.setOrderDate(updateDto.getOrderDate());
                  order.setStatus(updateDto.getStatus());
                  order.setCar(car);

                  if (updateDto.getMechanicIds() != null) {
                    if (updateDto.getMechanicIds().isEmpty()) {
                      order.setMechanics(new HashSet<>());
                    } else {
                      order.setMechanics(
                          new HashSet<>(
                              mechanicRepository.findAllById(updateDto.getMechanicIds())));
                    }
                  }
                  return repairOrderRepository.save(order);
                })
            .orElseThrow(() -> new RuntimeException("RepairOrder not found with id: " + id));

    return convertToDetailDto(repairOrder);
  }

  public void deleteOrder(Long id) {
    repairOrderRepository.deleteById(id);
  }

  private Car findOrCreateCar(CreateOrderDto.CarDto carDto) {
    if (carDto.getId() == null || carDto.getId() == 0) {
      Car car = new Car();
      car.setLicensePlate(carDto.getLicensePlate());
      car.setMake(carDto.getMake());
      car.setModel(carDto.getModel());
      return carRepository.save(car);
    } else {
      return carRepository
          .findById(carDto.getId())
          .orElseThrow(() -> new RuntimeException("Car not found with id: " + carDto.getId()));
    }
  }
}
