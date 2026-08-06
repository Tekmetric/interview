package com.interview.controller;

import com.interview.dto.CarDto;
import com.interview.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @Operation(summary = "Get all cars", description = "Returns a list of all cars that currently exist")
    @GetMapping("/api/car")
    public List<CarDto> getAllCars() {
        return carService.getAllCars();
    }

    @Operation(summary = "Get a single car", description = "Returns that car if it currently exists")
    @GetMapping("/api/car/{id}")
    public CarDto getCar(@PathVariable final UUID id) {
        return carService.getCar(id);
    }

    @Operation(summary = "Create a car", description = "Adds a new car to the database")
    @PostMapping("/api/car")
    public CarDto createCar(@RequestBody final CarDto carDto) {
        return carService.createCar(carDto);
    }

    @Operation(summary = "Updates a car", description = "Updates the information about a car")
    @PutMapping("/api/car/{id}")
    public CarDto updateCar(@PathVariable final UUID id, @RequestBody final CarDto carDto) {
        return carService.updateCar(id, carDto);
    }

    @Operation(summary = "Deletes a car", description = "Deletes a car by ID")
    @DeleteMapping("/api/car/{id}")
    public void deleteCar(@PathVariable final UUID id) {
        carService.deleteCar(id);
    }
}
