package com.interview.repository;

import com.interview.entity.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartRepository extends JpaRepository<PartEntity, Long> {
    @Modifying
    @Query("""
       update PartEntity p
       set p.inventory = p.inventory + :delta
       where p.id = :id
         and p.inventory + :delta >= 0
    """)
    int adjustInventory(@Param("id") Long id, @Param("delta") int delta);
}
