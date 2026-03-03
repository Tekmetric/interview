package com.interview.workorder.entity;

import com.interview.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_orders")
@Getter
@Setter
@NoArgsConstructor
public class WorkOrder extends BaseEntity {

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(nullable = false, length = 17)
    private String vin;

    @Column(name = "issue_description", nullable = false, length = 500)
    private String issueDescription;

    @Column(nullable = false, length = 30)
    private String status;
}
