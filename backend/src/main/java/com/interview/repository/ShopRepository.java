package com.interview.repository;

import com.interview.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    Optional<Shop> findByTitleAndDeletedDateIsNull(String title);

    Optional<Shop> findByIdAndDeletedDateIsNull(UUID id);

    Page<Shop> findByTitleContainingIgnoreCaseAndDeletedDateIsNull(Pageable pageable, String title);

}