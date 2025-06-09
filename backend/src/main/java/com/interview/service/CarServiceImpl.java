package com.interview.service;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import com.interview.mapper.CarMapper;
import com.interview.repository.CarRepository;
import com.interview.repository.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CarServiceImpl implements CarService {

  private final CarRepository carRepository;
  private final OwnerRepository ownerRepository;
  private final CarMapper carMapper;

  @Override
  public CarDTO getCarById(final Long id) {
    final Car existingCar =
        carRepository
            .findByIdWithOwner(id)
            .orElseThrow(() -> new EntityNotFoundException("Car not found"));

    return carMapper.toDto(existingCar);
  }

  @Override
  public CarDTO createCar(final CarCreateRequestDTO request) {
    final Long ownerId = request.getOwnerId();
    final Owner owner =
        ownerRepository
            .findById(ownerId)
            .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

    final Car car = carMapper.toEntity(request);

    final Car savedCar = carRepository.save(car);
    owner.addCar(savedCar);

    return carMapper.toDto(savedCar);
  }

  @Override
  public CarDTO deleteCarById(final Long id) {
    final Car existingCar = findCarOrThrow(id);

    carRepository.delete(existingCar);
    return carMapper.toDto(existingCar);
  }

  private Car findCarOrThrow(final Long id) {
    return carRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Car not found"));
  }
}
