package com.interview.repository;

import com.interview.entity.GameGridSquare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameGridSquareRepository extends JpaRepository<GameGridSquare, Long> {
  Optional<GameGridSquare> findByGameIdAndRowAndColumn(long gameId, int row, int column);
  long countByGameId(long gameId);
}
