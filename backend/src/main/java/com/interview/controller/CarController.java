package com.interview.controller;

import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Create a new car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<CarDto> createCar(@Valid @RequestBody CarDto carDto) {
        CarDto createdCar = carService.createCar(carDto);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all cars")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all cars",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class)) })
    })
    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Get a car by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the car",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        CarDto car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @Operation(summary = "Update a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id, @Valid @RequestBody CarDto carDto) {
        CarDto updatedCar = carService.updateCar(id, carDto);
        return ResponseEntity.ok(updatedCar);
    }

    @Operation(summary = "Delete a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all customers for a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all customers for the car",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content)
    })
    @GetMapping("/{id}/customers")
    public ResponseEntity<Set<CustomerDto>> getCarCustomers(@PathVariable Long id) {
        Set<CustomerDto> customers = carService.getCarOwners(id);
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Add a customer to a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer added to car",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Car or customer not found",
                    content = @Content)
    })
    @PostMapping("/{carId}/customers/{customerId}")
    public ResponseEntity<Void> addCarCustomer(@PathVariable Long carId, @PathVariable Long customerId) {
        carService.addOwnerToCar(carId, customerId);
        return ResponseEntity.ok().build();
    }
}
