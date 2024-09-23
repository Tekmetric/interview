package com.interview.repository;

import com.interview.model.VehicleRecall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRecallRepository extends JpaRepository<VehicleRecall, Long> {
}
