package com.interview.mapper;

import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.model.Car;
import com.interview.model.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    public Car toEntity(CarRequest request) {
        return Car.builder()
                .vin(request.vin())
                .brand(request.brand())
                .model(request.model())
                .manufacturedYear(request.manufacturedYear())
                .color(request.color())
                .fuelType(request.fuelType())
                .transmission(request.transmission())
                .basePrice(request.basePrice())
                .sellingPrice(request.sellingPrice())
                .status(request.status() != null ? request.status() : CarStatus.AVAILABLE)
                .build();
    }

    public void updateEntity(Car car, CarRequest request) {
        car.setVin(request.vin());
        car.setBrand(request.brand());
        car.setModel(request.model());
        car.setManufacturedYear(request.manufacturedYear());
        car.setColor(request.color());
        car.setFuelType(request.fuelType());
        car.setTransmission(request.transmission());
        car.setBasePrice(request.basePrice());
        car.setSellingPrice(request.sellingPrice());
        car.setStatus(request.status() != null ? request.status() : CarStatus.AVAILABLE);
    }

    public CarResponse toResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .vin(car.getVin())
                .brand(car.getBrand())
                .model(car.getModel())
                .manufacturedYear(car.getManufacturedYear())
                .color(car.getColor())
                .fuelType(car.getFuelType())
                .transmission(car.getTransmission())
                .basePrice(car.getBasePrice())
                .sellingPrice(car.getSellingPrice())
                .status(car.getStatus())
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }

    public Page<CarResponse> toResponsePage(Page<Car> carPage) {
        return carPage.map(this::toResponse);
    }
}
