package com.interview.repository;

import com.interview.entity.SnowReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnowReportRepository extends JpaRepository<SnowReport, Long> {
}
