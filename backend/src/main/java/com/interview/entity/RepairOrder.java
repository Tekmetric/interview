package com.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Design note on persisted totals vs. derived totals:
 *
 * We persist 'total' for each RepairOrder rather than calculating it at read time from RepairLineItems.
 *
 * Why?
 * (1) Read performance: We assume RepairOrders are read far more often than written. Persisting totals optimizes for the common read path.
 * (2) Simplicity: Avoids complex joins or subqueries when fetching orders with totals or N+1 query issues.
 * (3) Simpler queries for paginated endpoints: We want to avoid GROUP BY complexity or N+1 lookups to compute totals per row.
 *
 * Safeguards:
 * (1) Transactional updates: the service layer recomputes totals atomically whenever line items change.
 * (2) Optimistic locking (@Version): concurrent modifications will fail fast with a 409 Conflict
 * (3) Scheduled auditing: a periodic job verifies persisted totals against computed sums.
 *
 * This is a common trade-off: denormalize for read performance, then protect with consistency.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "repair_order", indexes = {
        @Index(name = "idx_order_number", columnList = "order_number", unique = true),
        @Index(name = "idx_repair_order_status", columnList = "status"),
        @Index(name = "idx_repair_order_vin", columnList = "vin"),
        @Index(name = "idx_repair_order_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This is our etag / optimistic locking field.
    // We use this in our service layer to prevent lost updates in concurrent scenarios. See RepairOrderService#verifyVersionHeader
    // For example, if two clients read the same version of a RepairOrder and both try to update it,
    // only the first update will succeed; the second will fail with a 409 Conflict due to version mismatch.
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name="order_number", nullable = false, unique = true, length = 32)
    @NotBlank @Size(min = 3, max = 32)
    private String orderNumber;

    // Vehicle info. In a real system, we might have a separate Vehicle entity and ref it here by VIN only instead of duplicating fields.
    @Column(name="vin", nullable = false, length = 17)
    @NotBlank @Size(min = 11, max = 17)
    private String vin;

    @Column(name="vehicle_year")
    private Integer vehicleYear;

    @Column(name="vehicle_make", length = 64)
    private String vehicleMake;

    @Column(name="vehicle_model", length = 64)
    private String vehicleModel;

    // Customer info. In a real system, we might have a separate Customer entity and ref it here by customerId only instead of duplicating fields.
    @Column(name="customer_name", length = 128)
    private String customerName;

    @Column(name="customer_phone", length = 32)
    @Pattern(regexp = "^[+0-9(). -]{7,32}$", message = "Invalid phone format")
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    private RepairOrderStatus status = RepairOrderStatus.OPEN;

    @Column(name="total", precision = 19, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @CreatedDate
    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RepairLineItem> lineItems = new ArrayList<>();

    public void addLineItem(RepairLineItem item) {
        item.setRepairOrder(this);
        lineItems.add(item);
    }
}
