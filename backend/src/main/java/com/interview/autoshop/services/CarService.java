package com.interview.autoshop.services;

import com.interview.autoshop.dto.CarDto;
import com.interview.autoshop.dto.create.CreateCarDto;
import com.interview.autoshop.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarService {

    Optional<CarDto> findById(Long id);

    Optional<Car> getEntityById(Long id);

    List<CarDto> list(String email);

    CarDto create(CreateCarDto carDto);

    CarDto update(Long id, CreateCarDto carDto);

    void delete(Long id);

    boolean isCarPresent(Long id);

}
