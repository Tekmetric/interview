package com.interview.service;

import com.interview.dto.SnowReportRequest;
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

    public SnowReport save(SnowReportRequest request) {
        log.info("Saving new snow report for mountain: {}", request.getMountainName());
        SnowReport snowReport = new SnowReport();
        snowReport.setMountainName(request.getMountainName());
        snowReport.setRegion(request.getRegion());
        snowReport.setCountry(request.getCountry());
        snowReport.setCurrentSnowTotal(request.getCurrentSnowTotal());
        return snowReportRepository.save(snowReport);
    }

    public SnowReport update(Long id, SnowReportRequest request) {
        log.info("Updating snow report with id: {}", id);
        SnowReport existing = snowReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Could not find snow report to update with id: {}", id);
                    return new SnowReportNotFoundException(id);
                });
        existing.setMountainName(request.getMountainName());
        existing.setRegion(request.getRegion());
        existing.setCountry(request.getCountry());
        existing.setCurrentSnowTotal(request.getCurrentSnowTotal());
        return snowReportRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Deleting snow report with id: {}", id);
        if (!snowReportRepository.existsById(id)) {
            log.warn("Could not find snow report to delete with id: {}", id);
            throw new SnowReportNotFoundException(id);
        }
        snowReportRepository.deleteById(id);
    }
}
