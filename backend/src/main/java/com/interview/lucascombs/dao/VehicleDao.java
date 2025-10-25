package com.interview.lucascombs.dao;

import com.interview.lucascombs.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDao extends JpaRepository<Vehicle, Long> {

}
