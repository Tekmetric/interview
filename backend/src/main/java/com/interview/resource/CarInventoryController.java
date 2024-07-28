package com.interview.resource;

import com.interview.model.Car;
import com.interview.service.CarService;
import java.util.List;
import javax.transaction.Transactional;
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
  public ResponseEntity addCar(@RequestBody Car car) {
    carService.saveCar(car);
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("/license/{license}")
  public ResponseEntity<Car> getCar(@PathVariable("license") String license) {
    Car car = carService.findCar(license);
    if (car == null) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(car, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<Car>> getCars() {
    return new ResponseEntity<>(carService.findCars(), HttpStatus.OK);
  }

  @DeleteMapping("/remove/license/{license}")
  @Transactional
  public ResponseEntity deleteCar(@PathVariable("license") String license) {
    carService.removeCar(license);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/update/license/{license}")
  public ResponseEntity updateCar(@PathVariable("license") String license, @RequestBody Car car) {
    Car originalCar = carService.findCar(license);
    if (originalCar == null) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    Car newCar = carService.updateCar(originalCar, car);

    return new ResponseEntity(newCar, HttpStatus.OK);
  }
}
