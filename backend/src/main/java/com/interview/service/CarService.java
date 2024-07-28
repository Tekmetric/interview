package com.interview.service;

import com.interview.model.response.CarResponse;
import com.interview.model.request.CarRequest;
import com.interview.model.Car;
import com.interview.repository.CarRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarService {

  @Autowired
  CarRepository carRepository;

  public void saveCar(CarRequest carRequest) {
    carRepository.save(
        new Car(
            carRequest.getMake(),
            carRequest.getModel(),
            carRequest.getProductionYear(),
            carRequest.getColor(),
            carRequest.getLicense()
        )
    );
  }

  public CarResponse findCar(String license) {
    Car car = carRepository.findByLicense(license);
    if (car == null) return null;

    return new CarResponse(
        car.getMake(),
        car.getModel(),
        car.getProductionYear(),
        car.getColor(),
        car.getLicense());
  }

  public List<CarResponse> findCars() {
    List<Car> cars = carRepository.findAll();

    List<CarResponse> carsResponse= cars.stream()
        .map(car -> new CarResponse(car))
        .collect(Collectors.toList());

    return carsResponse;
  }

  public void removeCar(String license) {
    carRepository.deleteByLicense(license);
  }

  public CarResponse updateCar(String license, CarRequest newCar) {

    Car car = carRepository.findByLicense(license);
    if (car == null) return null;

    car.setMake(newCar.getMake());
    car.setColor(newCar.getColor());
    car.setModel(newCar.getModel());
    car.setProductionYear(newCar.getProductionYear());
    car.setLicense(newCar.getLicense());
    carRepository.save(car);

    return new CarResponse(car.getMake(), car.getModel(), car.getProductionYear(), car.getColor(), car.getLicense());
  }

}
