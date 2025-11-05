package com.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(
        name = "work_order_parts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"work_order_id","part_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class WorkOrderPartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrderEntity workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private PartEntity part;

    @Column(nullable = false)
    private int partCount = 0;
}
