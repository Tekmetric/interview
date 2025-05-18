package com.interview.vehicle.persistence.respository;

import com.interview.vehicle.model.VehicleId;
import com.interview.vehicle.persistence.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    @Query("SELECT v FROM VehicleEntity v WHERE v.id = :#{#id.value}")
    Optional<VehicleEntity> findById(@Param("id") VehicleId id);

    @Modifying
    @Query("DELETE FROM VehicleEntity v WHERE v.id = :#{#id.value}")
    void deleteById(@Param("id") VehicleId id);
}
