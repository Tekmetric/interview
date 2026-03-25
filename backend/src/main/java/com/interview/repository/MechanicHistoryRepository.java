package com.interview.repository;

import com.interview.model.audit.MechanicHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MechanicHistoryRepository extends JpaRepository<MechanicHistory, Long> {
}
