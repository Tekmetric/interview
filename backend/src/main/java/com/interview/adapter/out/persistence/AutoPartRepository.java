package com.interview.adapter.out.persistence;

import com.interview.adapter.out.persistence.entities.AutoPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AutoPartRepository extends JpaRepository<AutoPartEntity, UUID> {
}
