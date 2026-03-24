package com.interview.repository;

import com.interview.model.audit.MechanicShopHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MechanicShopHistoryRepository extends JpaRepository<MechanicShopHistory, Long> {
}
