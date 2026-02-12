package com.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "repair_line_item", indexes = {
        @Index(name = "idx_line_item_order_id", columnList = "repair_order_id"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairLineItem {

    // In a real system, we might have a separate Repair entity and ref it here by unique id instead of using an
    // incrementing id and duplicating description and price fields.
    // For example, if we had a catalog of standard repairs and services, we would want to reference those instead of duplicating data.
    // But if prices of those repairs changed over time, we might want to keep historical accuracy of line items and pursue a hybrid approach.
    // This design choice depends on business requirements so for simplicity we just store the data directly here, denormalized.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @NotBlank
    @Column(name="description", nullable = false, length = 256)
    private String description;

    @NotNull @Min(1)
    @Column(name="quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name="unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name="line_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal lineTotal;
}
