package com.interview.service;

import com.interview.model.Car;
import com.interview.repository.CarRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarService {

  @Autowired
  CarRepository carRepository;

  public void saveCar(Car car) {
    carRepository.save(
        new Car(
            car.getMake(),
            car.getModel(),
            car.getProductionYear(),
            car.getColor(),
            car.getLicense()
        )
    );
  }

  public Car findCar(String license) {
    return carRepository.findByLicense(license);
  }

  public List<Car> findCars() {
    return carRepository.findAll();
  }

  public void removeCar(String license) {
    carRepository.deleteByLicense(license);
  }

  public Car updateCar(Car car, Car newCar) {
    car.setMake(newCar.getMake());
    car.setColor(newCar.getColor());
    car.setModel(newCar.getModel());
    car.setProductionYear(newCar.getProductionYear());
    car.setLicense(newCar.getLicense());

    carRepository.save(car);

    return car;
  }

}
