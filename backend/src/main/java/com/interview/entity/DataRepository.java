package com.interview.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<DataRecord, UUID> {

    List<DataRecord> findAll();

    Optional<DataRecord> findOneById(UUID id);

    void deleteById(UUID id);

}
