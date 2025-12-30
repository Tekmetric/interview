package com.interview.repository;

import com.interview.entity.RepairLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairLineItemRepository extends JpaRepository<RepairLineItem, Long> {}
