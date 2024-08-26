package com.interview.controller;

import com.interview.dto.CarCreateDTO;
import com.interview.entity.Car;
import com.interview.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping
    private ResponseEntity<Car> createNewCar(@RequestBody CarCreateDTO car, UriComponentsBuilder uriBuilder) {
        Car savedCar = carService.createCar(car);
        URI location = uriBuilder
                .path("/api/cars/{id}")
                .buildAndExpand(savedCar.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{carId}")
    private ResponseEntity<Car> getCarById(@PathVariable Long carId) {
        return ResponseEntity.ok(carService.getCarById(carId));
    }

    @GetMapping
    private ResponseEntity<Page<Car>> findAll(Pageable pageable) {
        Page<Car> page = carService.findAllCars(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"))));

        return ResponseEntity.ok(page);
    }

    @PutMapping("{carId}")
    public ResponseEntity<Void> updateCar(@PathVariable Long carId, @RequestBody CarCreateDTO car) {
        carService.updateCar(carId, car);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }
}
