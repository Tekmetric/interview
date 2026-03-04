package com.interview.workorder.entity;

import com.interview.common.entity.BaseEntity;
import com.interview.customer.entity.Customer;
import com.interview.workorder.model.WorkOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 17)
    private String vin;

    @Column(name = "issue_description", nullable = false, length = 500)
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkOrderStatus status;
}
