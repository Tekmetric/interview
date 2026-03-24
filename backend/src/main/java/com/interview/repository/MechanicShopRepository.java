package com.interview.repository;

import com.interview.model.MechanicShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MechanicShopRepository extends JpaRepository<MechanicShop, Long> {

    Optional<MechanicShop> findWithMechanicsById(Long mechanicId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
