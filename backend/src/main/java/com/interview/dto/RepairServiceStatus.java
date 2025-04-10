package com.interview.dto;

/**
 * Enum representing the possible statuses of a repair service.
 */
public enum RepairServiceStatus {
    PENDING,       // Initial status when service is first requested
    DIAGNOSED,     // Vehicle has been diagnosed
    APPROVED,      // Customer has approved the repair
    IN_PROGRESS,   // Repair work is in progress
    COMPLETED,     // Repair work is completed
    DELIVERED,     // Vehicle has been delivered back to customer
    CANCELLED      // Service was cancelled
}
