package com.interview.service;

import com.interview.dto.CarCreateDTO;
import com.interview.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface CarService {
    Car getCarById(long id);

    Car createCar(CarCreateDTO car);

    void updateCar(long id, CarCreateDTO car);

    void deleteCar(long carId);

    Page<Car> findAllCars(PageRequest name);
}
