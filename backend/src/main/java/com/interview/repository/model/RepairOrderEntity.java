package com.interview.repository.model;

import com.interview.model.EstimationStatus;
import com.interview.model.RepairOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "vin", nullable = false)
    private String vin;

    @Column(name = "car_model", nullable = false)
    private String carModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RepairOrderStatus status;

    @Column(name = "issue_description", nullable = false)
    private String issueDescription;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "repairOrderEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @SQLRestriction("is_deleted = false")
    private List<WorkItemEntity> workItems = new ArrayList<>();

    @Column(name = "estimation_pdf_object_key")
    private String estimationPdfObjectKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "estimation_status")
    private EstimationStatus estimationStatus;

    public void addWorkItem(WorkItemEntity workItem) {
        workItem.setRepairOrderEntity(this);
        workItems.add(workItem);
    }

    public void removeWorkItem(WorkItemEntity workItem) {
        if (workItems.remove(workItem)) {
            workItem.setRepairOrderEntity(null);
        }
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

