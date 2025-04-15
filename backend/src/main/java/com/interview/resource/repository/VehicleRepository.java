package com.interview.resource.repository;

import com.interview.resource.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
  @Query("SELECT v FROM Vehicle v WHERE " + "LOWER(v.vin) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(v.make) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%')) OR "
      + "CAST(v.modelYear AS string) LIKE %:query%")
  Page<Vehicle> searchByQuery(@Param("query") String query, Pageable pageable);
}
