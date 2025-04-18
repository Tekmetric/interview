package com.interview.repository;

import com.interview.model.db.Car;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface CarRepository extends CrudRepository<Car, Integer> {

    Optional<Car> findCarByVin(String vin);
}
