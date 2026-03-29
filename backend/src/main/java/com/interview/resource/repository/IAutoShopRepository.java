package com.interview.resource.repository;

import com.interview.resource.entity.AutoShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAutoShopRepository extends JpaRepository<AutoShop, Long> {
}
