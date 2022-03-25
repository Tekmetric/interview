package com.interview.repository;


import com.interview.entity.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Inventory> findByIdAndDeletedAtIsNull(Long id);

}
