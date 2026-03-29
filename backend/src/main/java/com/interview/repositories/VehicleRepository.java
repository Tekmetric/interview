package com.interview.repositories;

import com.interview.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaSpecificationExecutor<Vehicle>, PagingAndSortingRepository<Vehicle, Long>,
        CrudRepository<Vehicle, Long>, JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVin(String vin);

    boolean existsByVinAndIdNot(String vin, Long id);
}
