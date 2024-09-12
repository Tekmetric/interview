package com.interview.repositories;

import com.interview.entities.TeeTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeeTimeRepository extends JpaRepository<TeeTime, Long> {
}
