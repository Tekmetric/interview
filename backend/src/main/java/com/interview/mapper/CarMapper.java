package com.interview.mapper;

import com.interview.dto.CarDto;
import com.interview.model.Car;
import com.interview.model.Customer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CarMapper {

    public CarDto toDto(Car car) {
        if (car == null) {
            return null;
        }
        return new CarDto(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getModelYear(),
                car.getColor(),
                car.getVin(),
                car.getCustomers().stream().map(Customer::getId).collect(Collectors.toSet())
        );
    }

    public Car toEntity(CarDto carDto) {
        if (carDto == null) {
            return null;
        }
        Car car = new Car();
        car.setId(carDto.getId());
        car.setMake(carDto.getMake());
        car.setModel(carDto.getModel());
        car.setModelYear(carDto.getModelYear());
        car.setColor(carDto.getColor());
        car.setVin(carDto.getVin());
        return car;
    }
}
