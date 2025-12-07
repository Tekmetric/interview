package com.tekmetric.repository;

import com.tekmetric.entity.Car;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CarDAO extends JpaRepository<Car, UUID>, JpaSpecificationExecutor<Car> {
  @Modifying
  @Transactional
  @Query(
      """
      update Car c
         set c.color   = COALESCE(:color, c.color),
             c.ownerId = COALESCE(:ownerId, c.ownerId)
       where c.id = :id
      """)
  int updateOwnerAndColor(UUID id, UUID ownerId, String color);
}
