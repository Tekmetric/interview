package com.interview.data.repository.jpa;

import com.interview.data.entity.ListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ListItemJpaRepository extends JpaRepository<ListItemEntity, Long> {
    @Query(value = "SELECT * FROM LIST_ITEM WHERE SHOPPING_LIST_ID = :shoppingListId", nativeQuery = true)
    List<ListItemEntity> findByShoppingListId(Long shoppingListId);

    @Modifying
    @Query(value = "DELETE FROM LIST_ITEM WHERE SHOPPING_LIST_ID = :shoppingListId", nativeQuery = true)
    void deleteByShoppingListId(Long shoppingListId);
}
