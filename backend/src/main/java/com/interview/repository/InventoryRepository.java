package com.interview.repository;


import com.interview.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

    Page<Inventory> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Inventory> findByIdAndDeletedAtIsNull(Long id);

    List<Inventory> findAllByIdInAndDeletedAtIsNull(List<Long> ids);
}
