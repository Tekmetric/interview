package com.interview.autoshop.controllers;

import com.interview.autoshop.dto.CarDto;
import com.interview.autoshop.dto.create.CreateCarDto;
import com.interview.autoshop.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(final CarService carService){
        this.carService = carService;
    }

    @GetMapping(path = "/api/cars/{id}")
    public ResponseEntity<CarDto> getCar(@PathVariable Long id){
        Optional<CarDto> carDto = carService.findById(id);
        if(carDto.isPresent()){
            return new ResponseEntity<CarDto>(carDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<CarDto>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/api/cars")
    public ResponseEntity<List<CarDto>> getCars(@RequestParam(defaultValue = "") String email) {
        List<CarDto> cars = carService.list(email);
        return new ResponseEntity<List<CarDto>>(cars, HttpStatus.OK);
    }

    @PostMapping(path = "/api/cars")
    public ResponseEntity<CarDto> createCar(@RequestBody CreateCarDto carDto){
        CarDto savedCarDto = carService.create(carDto);
        return new ResponseEntity<CarDto>(savedCarDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/api/cars/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id, @RequestBody CreateCarDto carDto){
        CarDto updatedCarDto = carService.update(id, carDto);
        return new ResponseEntity<CarDto>(updatedCarDto, HttpStatus.OK);
    }

    @DeleteMapping(path = "/api/cars/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id){
        carService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
