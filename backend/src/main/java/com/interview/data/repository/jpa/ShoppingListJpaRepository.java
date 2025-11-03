package com.interview.data.repository.jpa;

import com.interview.data.entity.ShoppingListEntity;
import com.interview.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListJpaRepository extends JpaRepository<ShoppingListEntity, Long> {

    @Query(value = "SELECT * FROM SHOPPING_LIST WHERE USER_ID = :userId", nativeQuery = true)
    List<ShoppingListEntity> findByUserId(@Param("userId") Long userId);
}
