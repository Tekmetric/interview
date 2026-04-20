package com.interview.repository;

import com.interview.model.LineItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem, UUID> {

  List<LineItem> findByRepairOrderId(UUID repairOrderId);
}
