package com.interview.service;

import static com.interview.service.ServiceUtils.toPageResponseDTO;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import com.interview.mapper.CarMapper;
import com.interview.repository.CarRepository;
import com.interview.repository.CarSpecification;
import com.interview.repository.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CarServiceImpl implements CarService {

  private final CarRepository carRepository;
  private final OwnerRepository ownerRepository;
  private final CarMapper carMapper;

  @Transactional(readOnly = true)
  @Override
  public CarDTO getCarById(final Long id) {
    final Car existingCar = findCarOrThrow(id);
    return carMapper.toDto(existingCar);
  }

  @Override
  public CarDTO createCar(final CarCreateRequestDTO request) {
    final Long ownerId = request.getOwnerId();
    final Owner owner = findOwnerOrThrow(ownerId);

    final Car car = carMapper.toEntity(request);

    final Car savedCar = carRepository.save(car);
    owner.addCar(savedCar);

    return carMapper.toDto(savedCar);
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponseDTO<CarDTO> getCars(final String query, final Pageable pageable) {
    final Specification<Car> specification = CarSpecification.fuzzySearch(query);
    final Page<CarDTO> page = carRepository.findAll(specification, pageable).map(carMapper::toDto);
    return toPageResponseDTO(page);
  }

  @Override
  public CarDTO deleteCarById(final Long id) {
    final Car existingCar = findCarOrThrow(id);
    final Owner owner = existingCar.getOwner();
    owner.removeCar(existingCar);

    carRepository.delete(existingCar);
    return carMapper.toDto(existingCar);
  }

  private Car findCarOrThrow(final Long id) {
    return carRepository
        .findByIdWithOwner(id)
        .orElseThrow(() -> new EntityNotFoundException("Car not found"));
  }

  private Owner findOwnerOrThrow(final Long ownerId) {
    return ownerRepository
        .findById(ownerId)
        .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
  }
}
