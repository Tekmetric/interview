package com.interview.repository;

import com.interview.model.db.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CarRepository extends CrudRepository<Car, Integer> {

    Optional<Car> findCarByVin(String vin);
}
