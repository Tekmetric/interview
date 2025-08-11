package com.interview.jpa.repository;

import com.interview.jpa.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlaneRepository extends JpaRepository<Plane, Integer>, JpaSpecificationExecutor<Plane> {
}
