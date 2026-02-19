package com.interview.service;

import com.interview.entity.SnowReport;
import com.interview.exception.SnowReportNotFoundException;
import com.interview.repository.SnowReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SnowReportService {

    private static final Logger log = LoggerFactory.getLogger(SnowReportService.class);

    private final SnowReportRepository snowReportRepository;

    public SnowReportService(SnowReportRepository snowReportRepository) {
        this.snowReportRepository = snowReportRepository;
    }

    public Page<SnowReport> findAll(Pageable pageable) {
        log.debug("Fetching snow reports - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return snowReportRepository.findAll(pageable);
    }

    public Optional<SnowReport> findById(Long id) {
        log.debug("Fetching snow report with id: {}", id);
        return snowReportRepository.findById(id);
    }

    public SnowReport save(SnowReport snowReport) {
        log.info("Saving new snow report for mountain: {}", snowReport.getMountainName());
        return snowReportRepository.save(snowReport);
    }

    public SnowReport update(Long id, SnowReport updated) {
        log.info("Updating snow report with id: {}", id);
        SnowReport existing = snowReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Could not find snow report to update with id: {}", id);
                    return new SnowReportNotFoundException(id);
                });
        existing.setMountainName(updated.getMountainName());
        existing.setRegion(updated.getRegion()); // Region can be null!
        existing.setCountry(updated.getCountry());
        existing.setCurrentSnowTotal(updated.getCurrentSnowTotal());
        return snowReportRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Deleting snow report with id: {}", id);
        snowReportRepository.deleteById(id);
    }
}
