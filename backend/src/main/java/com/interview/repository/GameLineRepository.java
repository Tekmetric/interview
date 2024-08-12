package com.interview.repository;

import com.interview.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GameLineRepository extends JpaRepository<GameLine, Long> {
    Optional<GameLine> findByGameIdAndIsRowAndLineId(Long gameId, boolean isRow, int lineId);
    long countByGameId(long gameId);
}
