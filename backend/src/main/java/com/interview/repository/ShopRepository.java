package com.interview.repository;

import com.interview.model.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByIdAndActiveTrue(Long id);

    List<Shop> findByActiveTrue(Pageable pageable);

}
