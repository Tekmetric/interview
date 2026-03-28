package com.interview.repository;

import com.interview.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    boolean existsByNameAndSupplierAndBatchNumber(String name,  String supplier, String batchNumber);

    List<Fruit> findBySupplier(String supplier);

    List<Fruit> findByBatchNumberAndSupplier(String batchNumber, String supplier);
}
