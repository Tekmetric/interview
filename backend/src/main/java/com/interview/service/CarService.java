package com.interview.service;

import com.interview.dto.CarDto;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.CarUpdateIdMismatchException;
import com.interview.exception.DuplicateCarException;
import com.interview.mapper.CarMapper;
import com.interview.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarService {

    private static final String DUPLICATE_MSG = "Car already exists with VIN %s";
    private static final String CAR_NOT_FOUND_MSG = "Car not found %s";
    private static final String CAR_ID_MISMATCH_MSG = "Cannot update car %s due to ID mismatch";

    private final CarMapper carMapper;
    private final CarRepository carRepository;

    public List<CarDto> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public CarDto getCar(@NotNull final UUID id) {
        return carRepository.findById(id)
                .map(carMapper::toDto)
                .orElseThrow(() -> new CarNotFoundException(String.format(CAR_NOT_FOUND_MSG, id)));
    }

    @Transactional
    public CarDto createCar(@NotNull final CarDto carDto) {
        try {
            final var added = carRepository.saveAndFlush(carMapper.toEntity(carDto));
            log.info("Successfully created car {}", added.getId());
            return carMapper.toDto(added);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCarException(String.format(DUPLICATE_MSG, carDto.vin()), e);
        }
    }

    @Transactional
    public CarDto updateCar(@NotNull final UUID id, @NotNull final CarDto carDto) {
        if (carDto.id() != null && !id.equals(carDto.id())) {
            throw new CarUpdateIdMismatchException(String.format(CAR_ID_MISMATCH_MSG, id));
        }

        final var existingCar = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(String.format(CAR_NOT_FOUND_MSG, id)));
        carMapper.updateEntityFromDto(carDto, existingCar);

        try {
            final var updated = carRepository.saveAndFlush(existingCar);
            log.info("Successfully updated car {}", updated.getId());
            return carMapper.toDto(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCarException(String.format(DUPLICATE_MSG, carDto.vin()), e);
        }
    }

    @Transactional
    public void deleteCar(@NotNull final UUID id) {
        final var existingCar = carRepository.findById(id).orElse(null);
        if (existingCar == null) {
            log.warn("Attempted to delete non-existent car for ID {}", id);
            return;
        }

        carRepository.delete(existingCar);
        log.info("Successfully deleted car {}", id);
    }
}
