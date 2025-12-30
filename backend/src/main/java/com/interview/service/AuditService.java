package com.interview.service;

import com.interview.entity.RepairOrder;
import com.interview.repository.RepairOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is a service demonstrating a periodic audit of repair order totals. Every 10 minutes, it scans all RepairOrders
 * and verifies that the stored total matches the computed sum of its line items.
 * If discrepancies are found, they are logged as warnings.
 *
 * This is an example of a background maintenance task that can be useful in systems where we might want periodic
 * verification of data.
 *
 * Since I intentionally made the decision to store totals for performance, this audit is our safeguard.
 *
 * In production we would log to an external audit system instead of the app logs.
 * We might also want to trigger an alert.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final RepairOrderRepository repo;

    public AuditService(RepairOrderRepository repo) { this.repo = repo; }

    @Value("${audit.totals.enabled:true}")
    private boolean auditEnabled;

    @Scheduled(cron = "${audit.totals.cron:0 0/10 * * * *}")
    @Transactional(readOnly = true)
    public void auditTotals() {
        if (!auditEnabled) return;

        List<RepairOrder> orders = repo.findAll();
        int discrepancies = 0;
        for (RepairOrder ro : orders) {
            BigDecimal expected = repo.computeSumForOrder(ro.getId());
            BigDecimal actual = ro.getTotal();
            if (expected.compareTo(actual) != 0) {
                discrepancies++;
                log.warn("Audit discrepancy: RepairOrder id={} orderNumber={} total={} expected={}",
                        ro.getId(), ro.getOrderNumber(), actual, expected);
            }
        }
        if (discrepancies == 0) {
            log.debug("Audit complete: no discrepancies found across {} orders.", orders.size());
        } else {
            log.info("Audit complete: {} discrepancy(ies) found.", discrepancies);
        }
    }
}
