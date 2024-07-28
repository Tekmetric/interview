package com.interview.controller;

import com.interview.model.request.CarRequest;
import com.interview.model.response.CarResponse;
import com.interview.service.CarService;
import java.util.List;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class CarInventoryController {

  @Autowired
  private CarService carService;

  @PostMapping("/save")
  public ResponseEntity addCar(@Valid @RequestBody CarRequest car) {
    carService.saveCar(car);
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("/license/{license}")
  public ResponseEntity<CarResponse> getCar(@PathVariable("license") String license) {
    CarResponse car = carService.findCar(license);
    if (car == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(car, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<CarResponse>> getCars() {
    return new ResponseEntity<>(carService.findCars(), HttpStatus.OK);
  }

  @DeleteMapping("/remove/license/{license}")
  @Transactional
  public ResponseEntity deleteCar(@PathVariable("license") String license) {
    carService.removeCar(license);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/update/license/{license}")
  public ResponseEntity<CarResponse> updateCar(@PathVariable("license") String license, @Valid @RequestBody CarRequest car) {
    CarResponse newCar = carService.updateCar(license, car);
    if (newCar == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
    return new ResponseEntity(newCar, HttpStatus.OK);
  }
}
