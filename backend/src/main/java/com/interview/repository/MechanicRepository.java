package com.interview.repository;

import com.interview.model.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {

    List<Mechanic> findAllByMechanicShopId(Long shopId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
