package com.interview.repository;

import com.interview.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s, COUNT(*) AS usageCount FROM Shop sh JOIN sh.suppliers s GROUP BY s ORDER BY usageCount DESC")
    List<Supplier> findMostUsedSupplier();
}