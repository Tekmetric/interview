package com.interview.service;

import com.interview.dto.CarCreateDTO;
import com.interview.entity.Car;
import com.interview.exception.CarAlreadyExists;
import com.interview.exception.CarNotFoundException;
import com.interview.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Override
    @Transactional(readOnly = true)
    public Car getCarById(long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    @Override
    @Transactional
    public Car createCar(CarCreateDTO carData) {
        var car = new Car(carData.getName());
        try {
            return carRepository.save(car);
        } catch (DataIntegrityViolationException e) {
            throw new CarAlreadyExists(carData.getName());
        }
    }

    @Override
    @Transactional
    public void updateCar(long id, CarCreateDTO car) {
        carRepository.findById(id)
                .map(c -> {
                    c.setName(car.getName());
                    carRepository.save(c);
                    return c;
                })
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteCar(long id) {
        var car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        carRepository.delete(car);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Car> findAllCars(PageRequest page) {
        return carRepository.findAll(page);
    }
}
