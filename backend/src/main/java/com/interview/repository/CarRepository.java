package com.interview.repository;

import com.interview.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
  Car findByLicense(String license);

  void deleteByLicense(String license);
}
