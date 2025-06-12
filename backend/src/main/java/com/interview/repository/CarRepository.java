package com.interview.repository;

import com.interview.entity.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

  @Query("SELECT c FROM Car c JOIN FETCH c.owner WHERE c.id = :id")
  Optional<Car> findByIdWithOwner(@Param("id") Long id);
}
