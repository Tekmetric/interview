package com.interview.resource;

import com.interview.entity.SnowReport;
import com.interview.exception.SnowReportNotFoundException;
import com.interview.service.SnowReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/snow-reports")
public class SnowReportResource {

    private final SnowReportService snowReportService;

    public SnowReportResource(SnowReportService snowReportService) {
        this.snowReportService = snowReportService;
    }

    @GetMapping
    public Page<SnowReport> findAll(Pageable pageable) {
        return snowReportService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public SnowReport findById(@PathVariable Long id) {
        return snowReportService.findById(id)
                .orElseThrow(() -> new SnowReportNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SnowReport create(@RequestBody SnowReport snowReport) {
        return snowReportService.save(snowReport);
    }

    @PutMapping("/{id}")
    public SnowReport update(@PathVariable Long id, @RequestBody SnowReport snowReport) {
        return snowReportService.update(id, snowReport);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        snowReportService.delete(id);
    }
}
