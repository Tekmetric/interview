package com.interview.dto;

import com.interview.model.RepairOrder;
import java.util.stream.Collectors;

public class OrderDtoMapper {
  public static DetailOrderDto convertToDetailDto(RepairOrder order) {
    DetailOrderDto dto = new DetailOrderDto();
    dto.setId(order.getId());
    dto.setOrderDate(order.getOrderDate());
    dto.setStatus(order.getStatus());

    if (order.getCar() != null) {
      DetailOrderDto.CarDto carDto = new DetailOrderDto.CarDto();
      carDto.setId(order.getCar().getId());
      carDto.setLicensePlate(order.getCar().getLicensePlate());
      carDto.setMake(order.getCar().getMake());
      carDto.setModel(order.getCar().getModel());
      dto.setCar(carDto);
    }

    if (order.getMechanics() != null) {
      dto.setMechanics(
          order.getMechanics().stream()
              .map(
                  mechanic -> {
                    DetailOrderDto.MechanicDto mechanicDto = new DetailOrderDto.MechanicDto();
                    mechanicDto.setId(mechanic.getId());
                    mechanicDto.setName(mechanic.getName());
                    mechanicDto.setSpecialization(mechanic.getSpecialization());
                    return mechanicDto;
                  })
              .collect(Collectors.toSet()));
    }
    return dto;
  }

  public static ListOrderDto convertToListDto(RepairOrder order) {
    ListOrderDto dto = new ListOrderDto();
    dto.setId(order.getId());
    dto.setOrderDate(order.getOrderDate());
    dto.setStatus(order.getStatus());

    if (order.getCar() != null) {
      ListOrderDto.CarDto carDto = new ListOrderDto.CarDto();
      carDto.setLicensePlate(order.getCar().getLicensePlate());
      carDto.setMake(order.getCar().getMake());
      carDto.setModel(order.getCar().getModel());
      dto.setCar(carDto);
    }

    if (order.getMechanics() != null) {
      dto.setMechanics(
          order.getMechanics().stream()
              .map(
                  mechanic -> {
                    ListOrderDto.MechanicDto mechanicDto = new ListOrderDto.MechanicDto();
                    mechanicDto.setName(mechanic.getName());
                    return mechanicDto;
                  })
              .collect(Collectors.toSet()));
    }
    return dto;
  }
}
